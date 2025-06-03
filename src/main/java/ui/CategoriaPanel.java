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
    private JLabel statusLabel;

    private GenericDAO<Categoria> categoriaDAO = new GenericDAO<>(Categoria.class);

    public CategoriaPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        JLabel nomeLabel = new JLabel("Nome da Categoria:");
        nomeField = new JTextField();

        salvarButton = new JButton("Salvar");
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        formPanel.add(nomeLabel);
        formPanel.add(nomeField);

        add(formPanel, BorderLayout.CENTER);
        add(salvarButton, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCategoria();
            }
        });
    }

    private void salvarCategoria() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            statusLabel.setText("Por favor, preencha o nome da categoria.");
            return;
        }

        Categoria categoria = new Categoria(nome);

        try {
            categoriaDAO.salvar(categoria);
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("Categoria salva com sucesso!");
            nomeField.setText("");
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro ao salvar categoria: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
