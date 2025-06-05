package ui;

import dao.GenericDAO;
import model.Transacao;
import model.Usuario;
import model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

public class HistoricoPanel extends JPanel {

    private JComboBox<String> tipoCombo;
    private JComboBox<String> categoriaCombo;
    private JSpinner dataInicioSpinner;
    private JSpinner dataFimSpinner;
    private JButton buscarButton;
    private JTable historicoTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);
    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);  // DAO de Categoria
    private Usuario usuarioLogado;

    public HistoricoPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        initComponents();
        carregarCategorias();  // Carregar categorias do banco
        carregarTabela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel do filtro
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tipo de transação (Receita ou Despesa)
        JLabel tipoLabel = new JLabel("Tipo:");
        tipoCombo = new JComboBox<>(new String[]{"Receita", "Despesa"});
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(tipoLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(tipoCombo, gbc);

        // Categoria
        JLabel categoriaLabel = new JLabel("Categoria:");
        categoriaCombo = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(categoriaLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(categoriaCombo, gbc);

        // Data de início
        JLabel dataInicioLabel = new JLabel("Data Início:");
        Date date = new Date();
        SpinnerDateModel dateModelInicio = new SpinnerDateModel(date, null, null, java.util.Calendar.DAY_OF_MONTH);  // Novo modelo para Data Início
        dataInicioSpinner = new JSpinner(dateModelInicio);
        JSpinner.DateEditor dateEditorInicio = new JSpinner.DateEditor(dataInicioSpinner, "yyyy-MM-dd");
        dataInicioSpinner.setEditor(dateEditorInicio);
        gbc.gridx = 0;
        gbc.gridy = 2;
        filterPanel.add(dataInicioLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(dataInicioSpinner, gbc);

        // Data de fim
        JLabel dataFimLabel = new JLabel("Data Fim:");
        SpinnerDateModel dateModelFim = new SpinnerDateModel(date, null, null, java.util.Calendar.DAY_OF_MONTH);  // Novo modelo para Data Fim
        dataFimSpinner = new JSpinner(dateModelFim);
        JSpinner.DateEditor dateEditorFim = new JSpinner.DateEditor(dataFimSpinner, "yyyy-MM-dd");
        dataFimSpinner.setEditor(dateEditorFim);
        gbc.gridx = 0;
        gbc.gridy = 3;
        filterPanel.add(dataFimLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(dataFimSpinner, gbc);

        // Botão de busca
        buscarButton = new JButton("Buscar");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        filterPanel.add(buscarButton, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // Tabela de histórico
        tableModel = new DefaultTableModel(new Object[]{"ID", "Valor", "Data", "Descrição", "Tipo", "Categoria"}, 0);
        historicoTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(historicoTable);

        add(tableScrollPane, BorderLayout.CENTER);

        // Status de erro/sucesso
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.SOUTH);

        // Listener do botão de buscar
        buscarButton.addActionListener(e -> buscarHistorico());
    }

    private void carregarCategorias() {
        categoriaCombo.removeAllItems();
        List<Categoria> categorias = categoriaDAO.listarTodos();  // Carregar categorias do banco
        for (Categoria categoria : categorias) {
            categoriaCombo.addItem(categoria.getNome());  // Adicionando as categorias cadastradas no ComboBox
        }
    }

    private void carregarTabela() {
        // Carregar dados na tabela. Exemplo:
        tableModel.setRowCount(0);
        List<Transacao> transacoes = transacaoDAO.listarTodos();
        for (Transacao t : transacoes) {
            if (t.getUsuario().equals(usuarioLogado)) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getValor(),
                        t.getData(),
                        t.getDescricao(),
                        t.getTipo(),
                        t.getCategoria().getNome()
                });
            }
        }
    }

    private void buscarHistorico() {
        String tipo = (String) tipoCombo.getSelectedItem();
        String categoria = (String) categoriaCombo.getSelectedItem();

        // Verificar as datas selecionadas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataInicio = null;
        String dataFim = null;

        if (dataInicioSpinner.getValue() != null) {
            dataInicio = sdf.format(dataInicioSpinner.getValue());
        }

        if (dataFimSpinner.getValue() != null) {
            dataFim = sdf.format(dataFimSpinner.getValue());
        }

        // Limpa a tabela antes de adicionar novos resultados
        tableModel.setRowCount(0);

        // Realizar busca com base nos filtros. Aqui, estamos apenas filtrando por tipo, mas você pode ajustar para filtrar de acordo com os outros campos também.
        List<Transacao> transacoes = transacaoDAO.listarTodos();

        // Adicionando logs para depuração
        System.out.println("Buscando transações... Filtros aplicados - Tipo: " + tipo + ", Categoria: " + categoria + ", Data Início: " + dataInicio + ", Data Fim: " + dataFim);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        boolean transacaoEncontrada = false;  // Flag para verificar se encontramos transações

        for (Transacao t : transacoes) {
            boolean match = true;

            // Verificar tipo
            if (tipo != null && !t.getTipo().toString().equalsIgnoreCase(tipo)) {
                match = false;
            }

            // Verificar categoria
            if (categoria != null && !t.getCategoria().getNome().equalsIgnoreCase(categoria)) {
                match = false;
            }

            // Verificar data de início
            if (dataInicio != null && t.getData().isBefore(LocalDate.parse(dataInicio, formatter))) {
                match = false;
            }

            // Verificar data de fim
            if (dataFim != null && t.getData().isAfter(LocalDate.parse(dataFim, formatter))) {
                match = false;
            }

            // Se a transação corresponde aos filtros, adiciona na tabela
            if (match && t.getUsuario().equals(usuarioLogado)) {
                transacaoEncontrada = true;
                System.out.println("Transação encontrada: " + t.getDescricao());  // Log para verificação
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getValor(),
                        t.getData(),
                        t.getDescricao(),
                        t.getTipo(),
                        t.getCategoria().getNome()
                });
            }
        }

        // Atualizando a tabela
        historicoTable.setModel(tableModel);

        // Verificação final se a tabela foi atualizada
        if (!transacaoEncontrada) {
            System.out.println("Nenhuma transação encontrada com os filtros aplicados.");
        }
    }
}
