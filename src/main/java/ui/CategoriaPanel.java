package ui;

import dao.GenericDAO;
import model.Categoria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoriaPanel extends JPanel {

    private JTextField nomeField;
    private JButton salvarButton;
    private JButton editarButton;
    private JButton excluirButton;
    private JLabel statusLabel;

    private JList<Categoria> categoriaList;
    private DefaultListModel<Categoria> listModel;

    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);

    public CategoriaPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel de formulário para adicionar/editar categoria
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nomeLabel = new JLabel("Nome da Categoria:");
        nomeField = new JTextField();
        salvarButton = new JButton("Salvar");

        formPanel.add(nomeLabel);
        formPanel.add(nomeField);
        formPanel.add(new JLabel());
        formPanel.add(salvarButton);

        add(formPanel, BorderLayout.NORTH);

        // Lista de categorias
        listModel = new DefaultListModel<>();
        categoriaList = new JList<>(listModel);
        categoriaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(categoriaList);

        add(scrollPane, BorderLayout.CENTER);

        // Botões de ação
        JPanel buttonPanel = new JPanel();
        editarButton = new JButton("Editar");
        excluirButton = new JButton("Excluir");
        buttonPanel.add(editarButton);
        buttonPanel.add(excluirButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Label de status
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.WEST);

        // Carregar categorias na lista
        carregarCategorias();

        // Listeners
        salvarButton.addActionListener(e -> salvarCategoria());
        editarButton.addActionListener(e -> editarCategoria());
        excluirButton.addActionListener(e -> excluirCategoria());
    }

    private void carregarCategorias() {
        listModel.clear();
        for (Categoria c : categoriaDAO.listarTodos()) {
            listModel.addElement(c);
        }
    }

    private void salvarCategoria() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            statusLabel.setText("Por favor, preencha o nome da categoria.");
            return;
        }

        Categoria selecionada = categoriaList.getSelectedValue();

        try {
            if (selecionada == null) {
                // Salvar nova categoria
                Categoria categoria = new Categoria(nome);
                categoriaDAO.salvar(categoria);
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Categoria salva com sucesso!");
            } else {
                // Atualizar categoria existente
                selecionada.setNome(nome);
                categoriaDAO.salvar(selecionada);
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Categoria atualizada com sucesso!");
            }
            nomeField.setText("");
            carregarCategorias();
            categoriaList.clearSelection();
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro ao salvar categoria: " + ex.getMessage());
        }
    }

    private void editarCategoria() {
        Categoria selecionada = categoriaList.getSelectedValue();
        if (selecionada == null) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Selecione uma categoria para editar.");
            return;
        }
        nomeField.setText(selecionada.getNome());
        statusLabel.setText("Edite o nome e clique em Salvar.");
    }

    private void excluirCategoria() {
        Categoria selecionada = categoriaList.getSelectedValue();
        if (selecionada == null) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Selecione uma categoria para excluir.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão da categoria \"" + selecionada.getNome() + "\"?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoriaDAO.excluir(selecionada);
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Categoria excluída!");
                nomeField.setText("");
                carregarCategorias();
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Erro ao excluir categoria: " + ex.getMessage());
            }
        }
    }
}
