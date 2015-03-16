package com.spark.app.ocb.test;

import junit.framework.TestCase;

/**
 * Created by sunghun.
 */
public class TestUtil extends TestCase {

    public void testMarkRatio(){

        int total = 20;
        for (int i=0; i<=total; i++) {
            int mark = (int) Math.floor((i / total) * 100);
            System.out.println(String.format("%d/%d = %d", i, total, mark));
        }

    }


}
