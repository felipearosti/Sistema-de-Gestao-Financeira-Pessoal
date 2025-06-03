package ui;

import dao.GenericDAO;
import model.Categoria;
import model.Transacao;
import model.Transacao.TipoTransacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricoPanel extends JPanel {

    private JComboBox<TipoTransacao> tipoCombo;
    private JComboBox<Categoria> categoriaCombo;
    private JTextField dataInicioField;
    private JTextField dataFimField;
    private JButton filtrarButton;
    private JTable tabela;
    private DefaultTableModel tabelaModel;
    private JLabel statusLabel;

    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);
    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);

    public HistoricoPanel() {
        initComponents();
        carregarCategorias();
        carregarTabela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JPanel filtrosPanel = new JPanel(new GridLayout(2, 5, 10, 5));

        filtrosPanel.add(new JLabel("Data Início (YYYY-MM-DD):"));
        dataInicioField = new JTextField();
        filtrosPanel.add(dataInicioField);

        filtrosPanel.add(new JLabel("Data Fim (YYYY-MM-DD):"));
        dataFimField = new JTextField();
        filtrosPanel.add(dataFimField);

        filtrosPanel.add(new JLabel("Tipo:"));
        tipoCombo = new JComboBox<>();
        tipoCombo.addItem(null); // para filtro "todos"
        for (TipoTransacao tipo : TipoTransacao.values()) {
            tipoCombo.addItem(tipo);
        }
        filtrosPanel.add(tipoCombo);

        filtrosPanel.add(new JLabel("Categoria:"));
        categoriaCombo = new JComboBox<>();
        categoriaCombo.addItem(null); // para filtro "todas"
        filtrosPanel.add(categoriaCombo);

        filtrarButton = new JButton("Filtrar");
        filtrosPanel.add(filtrarButton);

        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        add(filtrosPanel, BorderLayout.NORTH);

        tabelaModel = new DefaultTableModel(new Object[]{"ID", "Valor", "Data", "Descrição", "Tipo", "Categoria"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(tabelaModel);
        JScrollPane scrollPane = new JScrollPane(tabela);

        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        filtrarButton.addActionListener(e -> aplicarFiltro());
    }

    private void carregarCategorias() {
        List<Categoria> categorias = categoriaDAO.listarTodos();
        for (Categoria c : categorias) {
            categoriaCombo.addItem(c);
        }
    }

    private void carregarTabela() {
        List<Transacao> transacoes = transacaoDAO.listarTodos();
        atualizarTabela(transacoes);
    }

    private void aplicarFiltro() {
        String dataInicioStr = dataInicioField.getText().trim();
        String dataFimStr = dataFimField.getText().trim();
        TipoTransacao tipo = (TipoTransacao) tipoCombo.getSelectedItem();
        Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();

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

        List<Transacao> filtradas = transacoes.stream()
                .filter(t -> (dataInicio == null || !t.getData().isBefore(dataInicio)) &&
                        (dataFim == null || !t.getData().isAfter(dataFim)) &&
                        (tipo == null || t.getTipo() == tipo) &&
                        (categoria == null || t.getCategoria().equals(categoria)))
                .collect(Collectors.toList());

        atualizarTabela(filtradas);
        statusLabel.setText("Filtrado " + filtradas.size() + " transações.");
    }

    private void atualizarTabela(List<Transacao> transacoes) {
        tabelaModel.setRowCount(0);
        for (Transacao t : transacoes) {
            tabelaModel.addRow(new Object[]{
                    t.getId(),
                    t.getValor(),
                    t.getData(),
                    t.getDescricao(),
                    t.getTipo(),
                    t.getCategoria().getNome()
            });
        }
    }

    // Para mostrar corretamente o nome no combo box categoria
    @Override
    public String toString() {
        return "HistoricoPanel";
    }
}
