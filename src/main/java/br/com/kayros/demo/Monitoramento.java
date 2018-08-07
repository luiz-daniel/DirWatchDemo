package br.com.kayros.demo;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class Monitoramento {

  private final WatchService watchService;
  private final Map<WatchKey, Path> eventos;


  public Monitoramento(String diretorio) throws IOException {
    this.watchService = FileSystems.getDefault().newWatchService();
    this.eventos = new HashMap<>();

    Path dir = Paths.get(diretorio);

    this.registrarDiretorio(dir);
  }

  private void registrarDiretorio(Path dir) throws IOException {
    WatchKey evento = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    eventos.put(evento, dir);
  }

  public void iniciar() {

    while (true) {
      monitorarEventos();
    }

  }


  void monitorarEventos() {

    WatchKey chave;
    try {
      chave = watchService.take();
    } catch (InterruptedException x) {
      return;
    }

    Optional<Path> optionalDir = validarChave(chave);
    if(!optionalDir.isPresent())
        return;

    processarEventos(chave);

    removerChaveProcessada(chave);
  }

  private void removerChaveProcessada(WatchKey chave) {
    boolean valid = chave.reset();
    if (!valid) {
      eventos.remove(chave);
      if (eventos.isEmpty()) {
        System.exit(1);
      }
    }
  }

  private void processarEventos(WatchKey chave) {

    chave.pollEvents().forEach(this::printarEvento);

  }

  private void printarEvento(WatchEvent<?> evento) {
    WatchEvent<Path> ev = (WatchEvent<Path>) evento;
    Path name = ev.context();
    System.out.format("%s: %s\n", evento.kind().name(), name);

  }

  private Optional<Path> validarChave(WatchKey key) {
    Path dir = eventos.get(key);
    if (dir == null) {
      System.err.println("Nenhuma chave valida foi encontrada!!");
      return Optional.empty();
    }
    return Optional.of(dir);
  }

}
