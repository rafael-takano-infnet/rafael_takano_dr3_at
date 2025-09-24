package com.todoapp.service;

import com.todoapp.model.Tarefa;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaService {
    //Lista em memória pra armazenar as tarefas
    private List<Tarefa> tarefas = new ArrayList<>();

    // Adiciona uma nova tarefa
    public Tarefa criarTarefa(String titulo, String descricao) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        Tarefa novaTarefa = new Tarefa(titulo.trim(), descricao);
        tarefas.add(novaTarefa);
        return novaTarefa;
    }

    // Adiciona tarefa já criada (útil pra testes)
    public Tarefa criarTarefa(Tarefa tarefa) {
        if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        tarefas.add(tarefa);
        return tarefa;
    }

    // Busca todas as tarefas
    public List<Tarefa> buscarTodasTarefas() {
        return new ArrayList<>(tarefas); // Retorna uma cópia pra não alterar a original
    }

    //Busca tarefa por ID
    public Optional<Tarefa> buscarTarefaPorId(String id) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getId().equals(id))
                .findFirst();
    }

    // Atualiza status de conclusão
    public Optional<Tarefa> atualizarStatus(String id, boolean concluida) {
        Optional<Tarefa> tarefaOpt = buscarTarefaPorId(id);
        if (tarefaOpt.isPresent()) {
            tarefaOpt.get().setConcluida(concluida);
        }
        return tarefaOpt;
    }

    //Remove uma tarefa
    public boolean removerTarefa(String id) {
        return tarefas.removeIf(tarefa -> tarefa.getId().equals(id));
    }

    //Limpa todas as tarefas
    public void limparTarefas() {
        tarefas.clear();
    }

    // Conta quantas tarefas existem
    public int contarTarefas() {
        return tarefas.size();
    }
}