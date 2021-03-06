/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.icarocavalcanti.leitordecnis2;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private int altura = 10;

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
//        buscarRemuneracoes(documento);

    }

    public void buscarInformacoesPessoais(PDFTextStripperByArea stripper, PDPage pagina) throws IOException {

        int index = 0;
        //NIT
        palavra = "NIT:";
        stripper.addRegion("NIT", new Rectangle(
                50,
                121,
                100,
                altura
        ));

        //CPF
        palavra = "CPF:";
        stripper.addRegion("CPF", new Rectangle(
                258,
                121,
                100,
                altura
        ));

        //Nome
        palavra = "Nome:";
        stripper.addRegion("Nome", new Rectangle(
                388,
                121,
                200,
                altura
        ));

        //Data de nascimento
        palavra = "Data de nascimento:";
        stripper.addRegion("Data de nascimento", new Rectangle(
                116,
                136,
                100,
                altura
        ));

        //Nome da mãe
        palavra = "Nome da mãe:";
        stripper.addRegion("Nome da mae", new Rectangle(
                430,
                136,
                200,
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

        //localizar linha da Seq.
        int i = 1;
        while (letras.indexOf("Remunerações", i) >= 0) {

            int indexDaRemuneracao = letras.indexOf("Remunerações", i) + 111;

            int linha = eixoY.get(letras.indexOf("Seq", i) + 95).intValue();
            int linhaRem = eixoY.get(indexDaRemuneracao).intValue();

            int inicioDasRemuneracoes = linhaRem;
            int fimDasRemuneracoes;

            //verificar se tem remunerações
            int linhaDeVerificacao = eixoY.get(letras.indexOf("Remunerações", i)).intValue();

            stripper.addRegion("Remuneracoes", new Rectangle(
                    50,
                    linhaDeVerificacao,
                    100,
                    altura
            ));

            //--------------------------------------
            if (letras.indexOf("Seq", indexDaRemuneracao) > 0) {
                fimDasRemuneracoes = eixoY.get(letras.indexOf("Seq", indexDaRemuneracao)).intValue();

            } else {
                fimDasRemuneracoes = eixoY.get(letras.indexOf("O INSS", indexDaRemuneracao)).intValue();
            }

            int campoDasRemuneracoes = fimDasRemuneracoes - inicioDasRemuneracoes;

            //Seq.
            stripper.addRegion("Seq", new Rectangle(
                    42,
                    linha,
                    10,
                    altura
            ));

            //NIT
            stripper.addRegion("NIT", new Rectangle(
                    64,
                    linha,
                    70,
                    altura
            ));

            //Código Emp.
            stripper.addRegion("Codigo_Emp", new Rectangle(
                    142,
                    linha,
                    70,
                    altura
            ));

            //Origem do Vínculo
            stripper.addRegion("Origem_do_Vinculo", new Rectangle(
                    226,
                    linha,
                    200,
                    altura
            ));

            //Data de início
            largura = 50;
            stripper.addRegion("Data_Inicio", new Rectangle(
                    435,
                    linha,
                    largura,
                    altura
            ));

            //Data fim
            stripper.addRegion("Data_Fim", new Rectangle(
                    495,
                    linha,
                    50,
                    altura
            ));

            //Tipo Filiado no Vínculo
            stripper.addRegion("Tipo_Filiado_no_Vinculo", new Rectangle(
                    566,
                    linha,
                    100,
                    altura
            ));
            
            //Últ. Remun.
            stripper.addRegion("Ult_Remun", new Rectangle(
                    666,
                    linha,
                    50,
                    altura
            ));
            
            //Indicadores.
            stripper.addRegion("Indicadores", new Rectangle(
                    680,
                    linha,
                    100,
                    altura
            ));
            

            //Remunecarao
            stripper.addRegion("Remuneracao", new Rectangle(
                    42,
                    linhaRem,
                    800,
                    campoDasRemuneracoes
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
                    + "Ult. Remun.: " + stripper.getTextForRegion("Ult_Remun")
                    + "Indicadores: " + stripper.getTextForRegion("Indicadores")
                    
                    + "-----------------------------------------------------------------------------"
            //                    + "----| Remunerações:"
            //                    + stripper.getTextForRegion("Remuneracao")

            );

            if (stripper.getTextForRegion("Remuneracoes").contains("Remunerações")) {
//                System.out.println("Nessa linha tem remuneração");
                organizarRemuneracoes(stripper.getTextForRegion("Remuneracao"));
            } else {
                System.out.println("O vínculo não possui remunerações\n-----------------------------------------------------------------------------\n");
            }

            i += letras.indexOf("Remunerações");

        }

    }

    //está funcional, contudo, acredito que é mais seguro colocar a análise por áreas, assim seria possível dar maior segurança em relção aos indicadores.
    public void organizarRemuneracoes(String remuneracoes) {

        List<String> competencias = new ArrayList<>();
        List<String> contribuicoes = new ArrayList<>();
        String linhas[] = remuneracoes.split("\r\n");

        for (String linha : linhas) {
            if (!linha.isBlank()) {
                List<String> dados = Arrays.asList(linha.split(" "));

                for (String dado : dados) {

                    if (dado.contains("/")) {
                        competencias.add(dado);
                        System.out.print("Competência: " + dado);
                    } else {
                        contribuicoes.add(dado);
                        System.out.println(" Valor: " + dado);
                    }
                }
                {

                }
                {

                }

            }

        }
        
        System.out.println("-----------------------------------------------------------------------------\n");
    }

//    Arquivo para eventual necessidade (deverá ser excluído ao final do projeto).
//    public void buscarRemuneracoes(PDDocument documento) throws IOException {
//
//        PDFTextStripper tStripper = new PDFTextStripper();
//        tStripper.setSortByPosition(true);
//
//        String textoEmLinhas[] = tStripper.getText(documento).split("\r\n");
//
//        Boolean competencia = false;
//
//        for (String linha : textoEmLinhas) {
//
//            if (!linha.isBlank()) {
//
//                if (linha.contains("Seq.") || linha.contains("O INSS poderá")) {
//                    competencia = false;
//                }
//                if (competencia) {
//                    System.out.println(linha);
//                }
//                if (linha.contains("Competência")) {
//                    competencia = true;
//                }
//
//            }
//
//        }
//
//    }
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
