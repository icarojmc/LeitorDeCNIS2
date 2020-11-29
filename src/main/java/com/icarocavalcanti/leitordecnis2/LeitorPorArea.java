/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icarocavalcanti.leitordecnis2;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;


/**
 *
 * @author Ícaro
 */
public class LeitorPorArea {
    
    public static void main(String[] args) throws IOException{
        
        OrganizaTextoNaArea organizador = new OrganizaTextoNaArea();
        PDDocument documento = PDDocument.load(new File(System.getProperty("user.dir") + "\\src\\main\\java\\com\\icarocavalcanti\\leitordecnis2\\documentos\\extrato.pdf"));
        BuscaCaracterePorLocal.extrair(documento, organizador);

        organizador.buscar(documento);
        
        
        
        
    }
    
}
