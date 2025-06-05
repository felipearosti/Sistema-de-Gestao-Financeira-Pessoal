package ui;

import dao.GenericDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton cadastrarButton;
    private JLabel statusLabel;

    private GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);

    public LoginFrame() {
        setTitle("Login - Sistema Financeiro");
        setSize(350, 250);  // Ajustei o tamanho da tela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Para centralizar na tela

        initComponents();
    }

    private void initComponents() {
        // Usando um layout mais simples: BorderLayout
        setLayout(new BorderLayout(10, 10));

        // Painel central (para os campos e botões)
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // Layout mais simples
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espaçamento interno

        emailField = new JTextField();
        senhaField = new JPasswordField();
        loginButton = new JButton("Entrar");
        cadastrarButton = new JButton("Cadastrar");

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);

        // Adicionando os componentes ao painel
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);

        add(panel, BorderLayout.CENTER);  // Coloca o painel no centro da tela
        add(statusLabel, BorderLayout.NORTH);  // Exibe o status (erro/sucesso) acima dos campos

        // Botões (Coloca eles na parte inferior)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Alinha os botões ao centro
        buttonPanel.add(loginButton);
        buttonPanel.add(cadastrarButton);

        add(buttonPanel, BorderLayout.SOUTH);  // Coloca o painel com os botões no fundo da tela

        // Ações dos botões
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCadastro();
            }
        });

        // Pressionar "Enter" para fazer login
        senhaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
    }

    private void autenticar() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword()).trim();

        if (email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("Preencha email e senha.");
            return;
        }

        // Buscar o usuário no banco de dados
        for (Usuario u : usuarioDAO.listarTodos()) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Login realizado com sucesso!");
                abrirTelaPrincipal(u);  // Passando o usuário logado
                return;
            }
        }
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Email ou senha inválidos.");
    }

    private void abrirTelaPrincipal(Usuario usuario) {
        JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome() + "!");
        this.dispose();
        // Passando o nome do usuário para o MenuFrame
        new MenuFrame(usuario.getNome()).setVisible(true);
    }

    private void abrirCadastro() {
        this.dispose();
        new CadastroUsuarioFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
