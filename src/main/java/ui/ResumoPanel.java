package ui;

import dao.GenericDAO;
import model.Transacao;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

public class ResumoPanel extends JPanel {

    private JSpinner dataInicioSpinner;
    private JSpinner dataFimSpinner;
    private JButton calcularButton;
    private JLabel totalReceitasLabel;
    private JLabel totalDespesasLabel;
    private JLabel saldoLabel;

    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);
    private Usuario usuarioLogado;

    public ResumoPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel do filtro
        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Data de início
        JLabel dataInicioLabel = new JLabel("Data Início:");
        Date date = new Date();
        SpinnerDateModel dateModelInicio = new SpinnerDateModel(date, null, null, java.util.Calendar.DAY_OF_MONTH);  // Novo modelo para Data Início
        dataInicioSpinner = new JSpinner(dateModelInicio);
        JSpinner.DateEditor dateEditorInicio = new JSpinner.DateEditor(dataInicioSpinner, "yyyy-MM-dd");
        dataInicioSpinner.setEditor(dateEditorInicio);
        gbc.gridx = 0;
        gbc.gridy = 0;
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
        gbc.gridy = 1;
        filterPanel.add(dataFimLabel, gbc);
        gbc.gridx = 1;
        filterPanel.add(dataFimSpinner, gbc);

        // Botão de cálculo
        calcularButton = new JButton("Calcular");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        filterPanel.add(calcularButton, gbc);

        // Labels de total
        totalReceitasLabel = new JLabel("Total de Receitas: R$ 0,00");
        totalDespesasLabel = new JLabel("Total de Despesas: R$ 0,00");
        saldoLabel = new JLabel("Saldo: R$ 0,00");

        add(filterPanel, BorderLayout.NORTH);
        add(totalReceitasLabel, BorderLayout.CENTER);
        add(totalDespesasLabel, BorderLayout.CENTER);
        add(saldoLabel, BorderLayout.SOUTH);

        // Listener do botão de calcular
        calcularButton.addActionListener(e -> calcularResumo());
    }

    private void calcularResumo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataInicio = null;
        String dataFim = null;

        if (dataInicioSpinner.getValue() != null) {
            dataInicio = sdf.format(dataInicioSpinner.getValue());
        }

        if (dataFimSpinner.getValue() != null) {
            dataFim = sdf.format(dataFimSpinner.getValue());
        }

        // Log de depuração para verificação
        System.out.println("Buscando transações... Data Início: " + dataInicio + ", Data Fim: " + dataFim);

        // Filtros de datas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dataInicio, formatter);
        LocalDate endDate = LocalDate.parse(dataFim, formatter);

        double totalReceitas = 0.0;
        double totalDespesas = 0.0;

        // Buscar transações para o usuário logado e aplicar filtros
        List<Transacao> transacoes = transacaoDAO.listarTodos();
        for (Transacao t : transacoes) {
            if (t.getUsuario().equals(usuarioLogado)) {
                // Verifica se a data está dentro do intervalo
                if (!t.getData().isBefore(startDate) && !t.getData().isAfter(endDate)) {
                    if (t.getTipo().toString().equalsIgnoreCase("Receita")) {
                        totalReceitas += t.getValor();
                    } else if (t.getTipo().toString().equalsIgnoreCase("Despesa")) {
                        totalDespesas += t.getValor();
                    }
                }
            }
        }

        // Atualizar os labels com os valores
        double saldo = totalReceitas - totalDespesas;

        totalReceitasLabel.setText("Total de Receitas: R$ " + String.format("%.2f", totalReceitas));
        totalDespesasLabel.setText("Total de Despesas: R$ " + String.format("%.2f", totalDespesas));
        saldoLabel.setText("Saldo: R$ " + String.format("%.2f", saldo));
    }
}
