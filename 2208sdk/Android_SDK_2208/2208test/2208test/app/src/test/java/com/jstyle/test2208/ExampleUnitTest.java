package com.jstyle.test2208;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = "ExampleUnitTest";
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
         String.format("%.10f ",0.05f);
        byte[]value=new byte[]{0x4e, (byte) 0x38, (byte) 0x17,(byte) 0x0c};
        byte[]value2=new byte[]{0x42, (byte) 0xe3, (byte) 0xb9,(byte) 0x66};
        float fl=0;
        fl=value[3];


//        for(int i=0;i<value.length;i++){
//            System.out.print("addition_isCorrect: "+String.format("%.10f ",value[i])+"\n");
//        }
        System.out.print(getFloat(value,0)+"\n");
        System.out.print(getFloat(value2,0));
    }
    public static float getFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(getInt(arr, index));
    }
    public static int getInt(byte[] arr, int index) {
        return 	(0xff000000 	& (arr[index+0] << 24))  |
                (0x00ff0000 	& (arr[index+1] << 16))  |
                (0x0000ff00 	& (arr[index+2] << 8))   |
                (0x000000ff 	&  arr[index+3]);
    }
}