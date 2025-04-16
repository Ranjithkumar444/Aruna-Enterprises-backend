package com.arunaenterprisesbackend.ArunaEnterprises.Utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;

public class BarcodeGenerator {

    public static byte[] generateBarcodeImage(String barcodeText) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMatrix matrix = new MultiFormatWriter()
                .encode(barcodeText, BarcodeFormat.CODE_128, 300, 100);
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        return baos.toByteArray();
    }
}

