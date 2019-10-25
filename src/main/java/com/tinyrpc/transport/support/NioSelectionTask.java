package com.tinyrpc.transport.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NioSelectionTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(NioSelectionTask.class);

    private final static int SELECT_TIMEOUT_MILLISECONDS = 1000;

    private AbstractSelectableChannel ssc;

    private SelectorHolder selectorHolder;

    private SelectAction selectAction;

    public NioSelectionTask(AbstractSelectableChannel ssc, SelectorHolder selectorHolder, SelectAction selectAction) {
        this.ssc = ssc;
        this.selectorHolder = selectorHolder;
        this.selectAction = selectAction;
    }

    @Override
    public void run() {
        try {


            while (true){

                doSelect();

                Set<SelectionKey> keys = selectorHolder.selector().selectedKeys();

                if(keys.size() == 0){
                    continue;
                }
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()){

                    SelectionKey key = it.next();

                    it.remove();

                    if(!key.isValid()){
//                        this.close();
                        logger.info("channel invalid");
                        return;
                    }


                    int readyOps = key.readyOps();



                    if ((readyOps & SelectionKey.OP_ACCEPT) != 0) {
                        selectAction.onAccept(key);

                    }

                    if ((readyOps & SelectionKey.OP_CONNECT) != 0){
                        selectAction.onConnected(key);
                    }

                    if ((readyOps & SelectionKey.OP_READ) != 0) {
                        selectAction.onRead(key);
                    }

                    if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                        selectAction.onWrite(key);
                    }
                }

            }

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

    private void doSelect() throws IOException {

        int selectCnt = 0;

        for(;;){

            if(Thread.interrupted()){
                selectCnt = 0;
                break;
            }

            if(selectorHolder.selector().select(SELECT_TIMEOUT_MILLISECONDS) > 0){
                break;
            }

            long currentNanos = System.nanoTime();


                selectCnt ++;

                if(currentNanos - System.nanoTime() < TimeUnit.MILLISECONDS.toNanos(SELECT_TIMEOUT_MILLISECONDS) && selectCnt >= 512){
                    // 超时时间内处理完 有可能在空轮询

                    rebuildSelector();

                    selectorHolder.selector().selectNow();

                    selectCnt = 0;

                }

        }


    }

    private void rebuildSelector() throws IOException {
        final Selector newSelector = Selector.open();
        final Selector oldSelector = this.selectorHolder.selector();


        for (SelectionKey key: oldSelector.keys()) {
            Object a = key.attachment();
            try {

                //SelectionKey无效或者已经注册上了则跳过
                if (!key.isValid() || key.channel().keyFor(newSelector) != null) {
                    continue;
                }

                int interestOps = key.interestOps();
                key.cancel();
                key.channel().register(newSelector, interestOps, a);

            } catch (Exception e) {
                logger.warn("Failed to re-register a Channel to the new Selector.", e);
            }
        }

        this.selectorHolder.setSelector(newSelector);

        try {
            oldSelector.close();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }


    }

    public void close() {

        try {
            if(null!=selectorHolder){
                selectorHolder.selector().close();
            }
            if(null!=ssc){
                ssc.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

    }
}
