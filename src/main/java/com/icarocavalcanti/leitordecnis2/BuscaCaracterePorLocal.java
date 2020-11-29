/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icarocavalcanti.leitordecnis2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author Ícaro
 */
public class BuscaCaracterePorLocal extends PDFTextStripper {

    private static List<String> listaDeCaracteresComXeY = new ArrayList<>();

    public BuscaCaracterePorLocal() throws IOException {
    }
    
    public static void extrair(PDDocument file, OrganizaTextoNaArea organizar) throws IOException {

        PDFTextStripper stripper = new BuscaCaracterePorLocal();
        stripper.setSortByPosition(true);
        stripper.setStartPage(0);
        stripper.setEndPage(file.getNumberOfPages());

        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        stripper.writeText(file, dummy);
        
        organizar.registraCaracteres(listaDeCaracteresComXeY);

    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

        
        for (TextPosition texto : textPositions) {

            listaDeCaracteresComXeY.add(texto.getUnicode() + "&&&" + texto.getXDirAdj() + "&&&" + texto.getYDirAdj());

        }

        
    }

}
