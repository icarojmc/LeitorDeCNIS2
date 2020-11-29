/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icarocavalcanti.leitordecnis2;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author Ícaro
 */
public class OrganizaTextoNaArea {

    String letras;
    String palavra;
    List<Double> eixoX = new ArrayList<>();
    List<Double> eixoY = new ArrayList<>();

    private int largura = 100;
    private int altura = 5;

    private int numeroDaPagina;

    public void registraCaracteres(List<String> caracteres) {

        for (String caracter : caracteres) {

            if (!caracter.isBlank()) {

//            System.out.println(caracter);
                String caracteresComXeY[] = new String[3];
                caracteresComXeY = caracter.split("&&&");

                letras += caracteresComXeY[0];
                eixoX.add(Double.valueOf(caracteresComXeY[1]));
                eixoY.add(Double.valueOf(caracteresComXeY[2]));
            }

        }

    }

    public void buscar(PDDocument documento) throws IOException {

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDPage pagina = documento.getPage(numeroDaPagina);

        buscarInformacoesPessoais(stripper, pagina);
        buscarVinculos(stripper, pagina);

        buscarRemuneracoes(documento);

    }

    public void buscarInformacoesPessoais(PDFTextStripperByArea stripper, PDPage pagina) throws IOException {

        int index = 0;
        //NIT
        palavra = "NIT:";
        largura = 100;
        stripper.addRegion("NIT", new Rectangle(
                50,
                121,
                largura,
                altura
        ));

        //CPF
        palavra = "CPF:";
        largura = 100;
        stripper.addRegion("CPF", new Rectangle(
                258,
                121,
                largura,
                altura
        ));

        //Nome
        palavra = "Nome:";
        largura = 200;
        stripper.addRegion("Nome", new Rectangle(
                388,
                121,
                largura,
                altura
        ));

        //Data de nascimento
        palavra = "Data de nascimento:";
        largura = 100;
        stripper.addRegion("Data de nascimento", new Rectangle(
                116,
                136,
                largura,
                altura
        ));

        //Nome da mãe
        palavra = "Nome da mãe:";
        largura = 200;
        stripper.addRegion("Nome da mae", new Rectangle(
                388,
                136,
                largura,
                altura
        ));

        stripper.extractRegions(pagina);

        System.out.print("DADOS PESSOAIS: \n"
                + "NIT: " + stripper.getTextForRegion("NIT")
                + "CPF: " + stripper.getTextForRegion("CPF")
                + "Nome: " + stripper.getTextForRegion("Nome")
                + "Data de nascimento: " + stripper.getTextForRegion("Data de nascimento")
                + "Nome da mae: " + stripper.getTextForRegion("Nome da mae")
                + "-----------------------------------------------------------------------------\n"
        );

    }

    public void buscarVinculos(PDFTextStripperByArea stripper, PDPage pagina) throws IOException {

        int linha = 204;

        //Seq.
        palavra = "Seq";
        largura = 10;
        stripper.addRegion("Seq", new Rectangle(
                42,
                linha,
                largura,
                altura
        ));

        //NIT
        palavra = "NIT";
        largura = 70;
        stripper.addRegion("NIT", new Rectangle(
                64,
                linha,
                largura,
                altura
        ));

        //Código Emp.
        palavra = "Código Emp.";
        largura = 70;
        stripper.addRegion("Codigo_Emp", new Rectangle(
                142,
                linha,
                largura,
                altura
        ));

        //Origem do Vínculo
        palavra = "Origem do Vínculo";
        largura = 200;
        stripper.addRegion("Origem_do_Vinculo", new Rectangle(
                226,
                linha,
                largura,
                altura
        ));

        //Data de início
        palavra = "Data Início";
        largura = 50;
        stripper.addRegion("Data_Inicio", new Rectangle(
                435,
                linha,
                largura,
                altura
        ));

        //Data fim
        palavra = "Data Fim";
        largura = 50;
        stripper.addRegion("Data_Fim", new Rectangle(
                495,
                linha,
                largura,
                altura
        ));

        //Tipo Filiado no Vínculo
        palavra = "Tipo Filiado no Vínculo";
        largura = 100;
        stripper.addRegion("Tipo_Filiado_no_Vinculo", new Rectangle(
                566,
                linha,
                largura,
                altura
        ));

        //Ainda precisa ser colocado o NIT (descobrir como pular o do cabeçalho), a última remuneração e o indicador (ausentes no CNIS de estudo).
        stripper.extractRegions(pagina);

        System.out.println("VINCULO: \n" + "Seq: " + stripper.getTextForRegion("Seq")
                + "NIT: " + stripper.getTextForRegion("NIT")
                + "Código Emp.: " + stripper.getTextForRegion("Codigo_Emp")
                + "Origem do Vínculo: " + stripper.getTextForRegion("Origem_do_Vinculo")
                + "Data Início: " + stripper.getTextForRegion("Data_Inicio")
                + "Data Fim: " + stripper.getTextForRegion("Data_Fim")
                + "Tipo Filiado no Vínculo: " + stripper.getTextForRegion("Tipo_Filiado_no_Vinculo")
                + "-----------------------------------------------------------------------------\n");
    }

    public void buscarRemuneracoes(PDDocument documento) throws IOException {

        PDFTextStripper tStripper = new PDFTextStripper();
        tStripper.setSortByPosition(true);

        String textoEmLinhas[] = tStripper.getText(documento).split("\r\n");

        Boolean competencia = false;

        for (String linha : textoEmLinhas) {

            if (!linha.isBlank()) {

                if (linha.contains("Seq.") || linha.contains("O INSS poderá")) {
                    competencia = false;
//                System.out.println("");
                }
                if (competencia) {
                    System.out.println(linha);
                }
                if (linha.contains("Competência")) {
                    competencia = true;
                }

            }

        }

    }
}

//Arquivo para eventual necessidade de correção do local dos campos.
//public void buscarInformacoesPessoais(PDFTextStripperByArea stripper, PDPage pagina) throws IOException {
//
//        int index = 0;
//        //NIT
//        palavra = "NIT:";
//        largura = 100;
//        stripper.addRegion("NIT", new Rectangle(
//                eixoX.get(letra.indexOf(palavra, palavra.length() - 4)).intValue(),
//                eixoY.get(letra.indexOf(palavra, palavra.length() - 4)).intValue(),
//                largura,
//                altura
//        ));
//
//        //CPF
//        palavra = "CPF:";
//        largura = 100;
//        stripper.addRegion("CPF", new Rectangle(
//                eixoX.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                eixoY.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                largura,
//                altura
//        ));
//
//        //Nome
//        palavra = "Nome:";
//        largura = 200;
//        stripper.addRegion("Nome", new Rectangle(
//                eixoX.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                eixoY.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                largura,
//                altura
//        ));
//
//        //Data de nascimento
//        palavra = "Data de nascimento:";
//        largura = 100;
//        stripper.addRegion("Data de nascimento", new Rectangle(
//                eixoX.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                eixoY.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                largura,
//                altura
//        ));
//
//        //Nome da mãe
//        palavra = "Nome da mãe:";
//        largura = 200;
//        stripper.addRegion("Nome da mae", new Rectangle(
//                eixoX.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                eixoY.get(letra.indexOf(palavra) + palavra.length() - 4).intValue(),
//                largura,
//                altura
//        ));
//
//        stripper.extractRegions(pagina);
//
//        System.out.println("DADOS PESSOAIS: ");
//        System.out.println("NIT: " + stripper.getTextForRegion("NIT"));
//        System.out.println("CPF: " + stripper.getTextForRegion("CPF"));
//        System.out.println("Nome: " + stripper.getTextForRegion("Nome"));
//        System.out.println("Data de nascimento: " + stripper.getTextForRegion("Data de nascimento"));
//        System.out.println("Nome da mae: " + stripper.getTextForRegion("Nome da mae"));
//        System.out.println("-----------------------------------------------------------------------------");
//
//    }
//public void buscarVinculos(PDFTextStripperByArea stripper, PDPage pagina) throws IOException {
//        
//        //Seq.
//        palavra = "Seq";
//        largura = 10;
//        stripper.addRegion("Seq", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+95).intValue(),
//                eixoY.get(letra.indexOf(palavra)+95).intValue(),
//                largura,
//                altura
//        ));
//        
//        System.out.println(eixoX.get(letra.indexOf(palavra)+95));
//        System.out.println(eixoY.get(letra.indexOf(palavra)+95));
//        
//        
//        
//        
//        //Código Emp.
//        palavra = "Código Emp.";
//        largura = 70;
//        stripper.addRegion("Codigo_Emp", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+103).intValue(),
//                eixoY.get(letra.indexOf(palavra)+103).intValue(),
//                largura,
//                altura
//        ));
//        
//        //Origem do Vínculo
//        palavra = "Origem do Vínculo";
//        largura = 200;
//        stripper.addRegion("Origem_do_Vinculo", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+110).intValue(),
//                eixoY.get(letra.indexOf(palavra)+110).intValue(),
//                largura,
//                altura
//        ));
//        
//        //Data de início
//        palavra = "Data Início";
//        largura = 50;
//        stripper.addRegion("Data_Inicio", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+135).intValue(),
//                eixoY.get(letra.indexOf(palavra)+135).intValue(),
//                largura,
//                altura
//        ));
//        
//        //Data fim
//        palavra = "Data Fim";
//        largura = 50;
//        stripper.addRegion("Data_Fim", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+134).intValue(),
//                eixoY.get(letra.indexOf(palavra)+134).intValue(),
//                largura,
//                altura
//        ));
//        
//        //Tipo Filiado no Vínculo
//        palavra = "Tipo Filiado no Vínculo";
//        largura = 100;
//        stripper.addRegion("Tipo_Filiado_no_Vinculo", new Rectangle(
//                eixoX.get(letra.indexOf(palavra)+136).intValue(),
//                eixoY.get(letra.indexOf(palavra)+136).intValue(),
//                largura,
//                altura
//        ));
//        
//        //Ainda precisa ser colocado o NIT (descobrir como pular o do cabeçalho), a última remuneração e o indicador (ausentes no CNIS de estudo).
//        
//        stripper.extractRegions(pagina);
//        
//        System.out.println("Seq: " + stripper.getTextForRegion("Seq"));
//        System.out.println("Código Emp.: " + stripper.getTextForRegion("Codigo_Emp"));
//        System.out.println("Origem do Vínculo: " + stripper.getTextForRegion("Origem_do_Vinculo"));
//        System.out.println("Data Início: " + stripper.getTextForRegion("Data_Inicio"));
//        System.out.println("Data Fim: " + stripper.getTextForRegion("Data_Fim"));
//        System.out.println("Tipo Filiado no Vínculo: " + stripper.getTextForRegion("Tipo_Filiado_no_Vinculo"));
//        System.out.println("-----------------------------------------------------------------------------");
//    }
