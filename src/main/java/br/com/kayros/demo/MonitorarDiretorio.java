package br.com.kayros.demo;

import java.io.IOException;

public class MonitorarDiretorio {

  public static void main(String[] args) throws IOException {
    Monitoramento monitoramento = new Monitoramento("/Users/klsoares/Development/Estudos/teste-diretorio");
    monitoramento.iniciar();
  }

}
