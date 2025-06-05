package ui;

import dao.GenericDAO;
import model.Categoria;
import model.Transacao;
import model.Transacao.TipoTransacao;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TransacaoPanel extends JPanel {

    private JTextField valorField;
    private JTextField dataField;
    private JTextField descricaoField;
    private JComboBox<TipoTransacao> tipoCombo;
    private JComboBox<Categoria> categoriaCombo;
    private JButton salvarButton;
    private JButton editarButton;
    private JButton excluirButton;
    private JLabel statusLabel;

    private JTable transacaoTable;
    private DefaultTableModel tableModel;

    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);
    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);
    private GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);

    private Usuario usuarioLogado;

    public TransacaoPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        initComponents();
        carregarCategorias();
        carregarTabela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Valor:"));
        valorField = new JTextField();
        formPanel.add(valorField);

        formPanel.add(new JLabel("Data (YYYY-MM-DD):"));
        dataField = new JTextField();
        dataField.setText(LocalDate.now().toString());  // Preenche automaticamente com a data de hoje
        formPanel.add(dataField);

        formPanel.add(new JLabel("Descrição:"));
        descricaoField = new JTextField();
        formPanel.add(descricaoField);

        formPanel.add(new JLabel("Tipo:"));
        tipoCombo = new JComboBox<>(TipoTransacao.values());
        formPanel.add(tipoCombo);

        formPanel.add(new JLabel("Categoria:"));
        categoriaCombo = new JComboBox<>();
        formPanel.add(categoriaCombo);

        salvarButton = new JButton("Salvar");
        editarButton = new JButton("Editar");
        excluirButton = new JButton("Excluir");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(salvarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(excluirButton);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        // Tabela de transações
        tableModel = new DefaultTableModel(new Object[]{"ID", "Valor", "Data", "Descrição", "Tipo", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transacaoTable = new JTable(tableModel);  // Agora a tabela usa o DefaultTableModel corretamente
        JScrollPane tableScrollPane = new JScrollPane(transacaoTable);

        // Layout
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);

        // Listeners
        salvarButton.addActionListener(e -> salvarOuAtualizar());
        editarButton.addActionListener(e -> carregarParaEditar());
        excluirButton.addActionListener(e -> excluirTransacao());
    }

    private void carregarCategorias() {
        categoriaCombo.removeAllItems();
        List<Categoria> categorias = categoriaDAO.listarTodos();
        for (Categoria c : categorias) {
            categoriaCombo.addItem(c);  // Adicionando cada categoria no ComboBox
        }
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);  // Limpa a tabela antes de carregar as novas transações

        // Filtra transações por usuário logado
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

    private void salvarOuAtualizar() {
        String valorStr = valorField.getText().trim();
        String dataStr = dataField.getText().trim();
        String descricao = descricaoField.getText().trim();
        TipoTransacao tipo = (TipoTransacao) tipoCombo.getSelectedItem();
        Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();

        if (valorStr.isEmpty() || dataStr.isEmpty() || categoria == null || tipo == null) {
            statusLabel.setText("Preencha valor, data, tipo e categoria.");
            return;
        }

        double valor;
        LocalDate data;

        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Valor inválido.");
            return;
        }

        try {
            data = LocalDate.parse(dataStr);
        } catch (DateTimeParseException ex) {
            statusLabel.setText("Data inválida. Use formato YYYY-MM-DD.");
            return;
        }

        int selectedRow = transacaoTable.getSelectedRow();
        Transacao transacao;

        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            transacao = transacaoDAO.buscarPorId(id);
            if (transacao == null) {
                statusLabel.setText("Erro: transação não encontrada.");
                return;
            }
            transacao.setValor(valor);
            transacao.setData(data);
            transacao.setDescricao(descricao);
            transacao.setTipo(tipo);
            transacao.setCategoria(categoria);
            transacao.setUsuario(usuarioLogado);  // Associando o usuário logado corretamente
        } else {
            // Verificar se o usuarioLogado já existe no banco
            Usuario usuarioExistente = usuarioDAO.listarTodos().stream()
                    .filter(u -> u.getEmail().equals(usuarioLogado.getEmail()))
                    .findFirst()
                    .orElse(null);

            // Se o usuário já existir, usa ele; caso contrário, salva um novo usuário
            if (usuarioExistente != null) {
                usuarioLogado = usuarioExistente;  // Reutiliza o usuário existente
            } else {
                // Se o usuário não existir, salva o novo usuário
                usuarioDAO.salvar(usuarioLogado);
            }

            transacao = new Transacao(valor, data, descricao, tipo, categoria, usuarioLogado);  // Associando o usuário logado
        }

        try {
            transacaoDAO.salvar(transacao);
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("Transação salva com sucesso!");
            limparFormulario();
            carregarTabela();
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro ao salvar transação: " + ex.getMessage());
        }
    }

    private void carregarParaEditar() {
        int selectedRow = transacaoTable.getSelectedRow();
        if (selectedRow < 0) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Selecione uma transação para editar.");
            return;
        }

        valorField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        dataField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        descricaoField.setText(tableModel.getValueAt(selectedRow, 3).toString());

        TipoTransacao tipo = (TipoTransacao) tableModel.getValueAt(selectedRow, 4);
        tipoCombo.setSelectedItem(tipo);

        String categoriaNome = (String) tableModel.getValueAt(selectedRow, 5);
        for (int i = 0; i < categoriaCombo.getItemCount(); i++) {
            if (categoriaCombo.getItemAt(i).getNome().equals(categoriaNome)) {
                categoriaCombo.setSelectedIndex(i);
                break;
            }
        }
        statusLabel.setText("Editando transação selecionada.");
    }

    private void excluirTransacao() {
        int selectedRow = transacaoTable.getSelectedRow();
        if (selectedRow < 0) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Selecione uma transação para excluir.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirma exclusão da transação selecionada?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            Transacao transacao = transacaoDAO.buscarPorId(id);
            if (transacao == null) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Erro: transação não encontrada.");
                return;
            }
            try {
                transacaoDAO.excluir(transacao);
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Transação excluída com sucesso.");
                limparFormulario();
                carregarTabela();
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Erro ao excluir transação: " + ex.getMessage());
            }
        }
    }

    private void limparFormulario() {
        valorField.setText("");
        dataField.setText(LocalDate.now().toString());  // Preenche com a data de hoje novamente
        descricaoField.setText("");
        tipoCombo.setSelectedIndex(0);
        categoriaCombo.setSelectedIndex(-1);
        transacaoTable.clearSelection();
    }
}
