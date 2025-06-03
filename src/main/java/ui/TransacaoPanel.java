package ui;

import dao.GenericDAO;
import model.Categoria;
import model.Transacao;
import model.Transacao.TipoTransacao;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JLabel statusLabel;

    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);
    private GenericDAO<Transacao> transacaoDAO = new GenericDAO<>(Transacao.class);

    // Para teste, usar um usuário fixo. Depois pode adaptar para usuário logado.
    private Usuario usuarioLogado = new Usuario("teste@exemplo.com", "123456", "Felipe");

    public TransacaoPanel() {
        initComponents();
        carregarCategorias();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5,5));

        formPanel.add(new JLabel("Valor:"));
        valorField = new JTextField();
        formPanel.add(valorField);

        formPanel.add(new JLabel("Data (YYYY-MM-DD):"));
        dataField = new JTextField();
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
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        add(formPanel, BorderLayout.CENTER);
        add(salvarButton, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarTransacao();
            }
        });
    }

    private void carregarCategorias() {
        List<Categoria> categorias = categoriaDAO.listarTodos();
        categoriaCombo.removeAllItems();
        for(Categoria c : categorias) {
            categoriaCombo.addItem(c);
        }
    }

    private void salvarTransacao() {
        String valorStr = valorField.getText().trim();
        String dataStr = dataField.getText().trim();
        String descricao = descricaoField.getText().trim();
        TipoTransacao tipo = (TipoTransacao) tipoCombo.getSelectedItem();
        Categoria categoria = (Categoria) categoriaCombo.getSelectedItem();

        if(valorStr.isEmpty() || dataStr.isEmpty() || categoria == null || tipo == null) {
            statusLabel.setText("Preencha valor, data, tipo e categoria.");
            return;
        }

        double valor;
        LocalDate data;

        try {
            valor = Double.parseDouble(valorStr);
        } catch(NumberFormatException ex) {
            statusLabel.setText("Valor inválido.");
            return;
        }

        try {
            data = LocalDate.parse(dataStr);
        } catch(DateTimeParseException ex) {
            statusLabel.setText("Data inválida. Use formato YYYY-MM-DD.");
            return;
        }

        Transacao t = new Transacao(valor, data, descricao, tipo, categoria, usuarioLogado);

        try {
            transacaoDAO.salvar(t);
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("Transação salva com sucesso!");
            valorField.setText("");
            dataField.setText("");
            descricaoField.setText("");
        } catch(Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro ao salvar transação: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Para exibir corretamente o nome da categoria no combo box
    @Override
    public String toString() {
        return "TransacaoPanel";
    }
}
