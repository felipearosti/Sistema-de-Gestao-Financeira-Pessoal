package ui;

import dao.GenericDAO;
import model.Transacao;
import model.Transacao.TipoTransacao;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ResumoPanel extends JPanel {

    private JTextField dataInicioField;
    private JTextField dataFimField;
    private JButton calcularButton;
    private JLabel saldoLabel;
    private JLabel receitaLabel;
    private JLabel despesaLabel;
    private JLabel statusLabel;

    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);

    public ResumoPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(7, 1, 5, 5));

        add(new JLabel("Data Início (YYYY-MM-DD):"));
        dataInicioField = new JTextField();
        add(dataInicioField);

        add(new JLabel("Data Fim (YYYY-MM-DD):"));
        dataFimField = new JTextField();
        add(dataFimField);

        calcularButton = new JButton("Calcular Resumo");
        add(calcularButton);

        saldoLabel = new JLabel("Saldo Total: R$ 0,00");
        receitaLabel = new JLabel("Receitas: R$ 0,00");
        despesaLabel = new JLabel("Despesas: R$ 0,00");
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        add(saldoLabel);
        add(receitaLabel);
        add(despesaLabel);
        add(statusLabel);

        calcularButton.addActionListener(e -> calcularResumo());
    }

    private void calcularResumo() {
        String dataInicioStr = dataInicioField.getText().trim();
        String dataFimStr = dataFimField.getText().trim();

        LocalDate dataInicio = null;
        LocalDate dataFim = null;

        try {
            if (!dataInicioStr.isEmpty()) {
                dataInicio = LocalDate.parse(dataInicioStr);
            }
            if (!dataFimStr.isEmpty()) {
                dataFim = LocalDate.parse(dataFimStr);
            }
        } catch (DateTimeParseException ex) {
            statusLabel.setText("Data inválida. Use o formato YYYY-MM-DD.");
            return;
        }

        List<Transacao> transacoes = transacaoDAO.listarTodos();

        double totalReceitas = 0;
        double totalDespesas = 0;

        for (Transacao t : transacoes) {
            LocalDate data = t.getData();
            if ((dataInicio == null || !data.isBefore(dataInicio)) &&
                    (dataFim == null || !data.isAfter(dataFim))) {
                if (t.getTipo() == TipoTransacao.RECEITA) {
                    totalReceitas += t.getValor();
                } else if (t.getTipo() == TipoTransacao.DESPESA) {
                    totalDespesas += t.getValor();
                }
            }
        }

        double saldo = totalReceitas - totalDespesas;

        saldoLabel.setText(String.format("Saldo Total: R$ %.2f", saldo));
        receitaLabel.setText(String.format("Receitas: R$ %.2f", totalReceitas));
        despesaLabel.setText(String.format("Despesas: R$ %.2f", totalDespesas));
        statusLabel.setText("Resumo calculado com sucesso!");
    }
}
