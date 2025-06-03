package ui;

import dao.GenericDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JLabel statusLabel;

    private GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);

    public LoginFrame() {
        setTitle("Login - Sistema Financeiro");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza tela

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        emailField = new JTextField();
        senhaField = new JPasswordField();
        loginButton = new JButton("Entrar");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);

        add(panel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
    }

    private void autenticar() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if(email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("Preencha email e senha.");
            return;
        }

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Login realizado com sucesso!");
                abrirTelaPrincipal(u);
                return;
            }
        }
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Email ou senha inválidos.");
    }

    private void abrirTelaPrincipal(Usuario usuario) {
        // Aqui você pode abrir a janela principal do sistema passando o usuário logado
        // Por enquanto, só fecha o login para exemplificar
        JOptionPane.showMessageDialog(this, "Bem vindo, " + usuario.getNome() + "!");
        this.dispose();
    }

    public static void main(String[] args) {
        // Inicializa a interface no thread correto
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
