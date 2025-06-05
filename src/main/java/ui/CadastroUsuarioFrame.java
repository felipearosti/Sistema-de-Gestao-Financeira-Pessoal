package ui;

import dao.GenericDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroUsuarioFrame extends JFrame {

    private JTextField nomeField;
    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton cadastrarButton;
    private JButton voltarButton;  // Botão de voltar
    private JLabel statusLabel;

    private GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);

    public CadastroUsuarioFrame() {
        setTitle("Cadastro de Usuário");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));  // Aumentando para 6 para adicionar o botão de voltar
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        nomeField = new JTextField();
        emailField = new JTextField();
        senhaField = new JPasswordField();
        cadastrarButton = new JButton("Cadastrar");
        voltarButton = new JButton("Voltar");  // Inicializando o botão de voltar
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);

        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();  // Painel apenas para os botões
        buttonPanel.add(cadastrarButton);
        buttonPanel.add(voltarButton);  // Adicionando o botão "Voltar"
        add(buttonPanel, BorderLayout.SOUTH);

        add(statusLabel, BorderLayout.NORTH);

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });

        voltarButton.addActionListener(new ActionListener() {  // Ação do botão "Voltar"
            @Override
            public void actionPerformed(ActionEvent e) {
                voltarParaLogin();
            }
        });
    }

    private void cadastrarUsuario() {
        String nome = nomeField.getText().trim();
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword()).trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        // Validar formato de email
        if (!validarEmail(email)) {
            statusLabel.setText("Email inválido.");
            return;
        }

        // Verificar se o email já está cadastrado
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                statusLabel.setText("Este email já está cadastrado.");
                return;
            }
        }

        // Criar o novo usuário e salvar no banco
        Usuario novoUsuario = new Usuario(email, senha, nome);
        try {
            usuarioDAO.salvar(novoUsuario);
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("Usuário cadastrado com sucesso!");
            limparCampos();
            // Após o cadastro, redireciona para a tela principal do sistema, passando o nome do usuário
            new MenuFrame(nome).setVisible(true);
            this.dispose(); // Fecha a tela de cadastro
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            // Exibe a mensagem de erro detalhada
            statusLabel.setText("Erro ao cadastrar: " + ex.getMessage());
        }
    }

    private boolean validarEmail(String email) {
        // Expressão regular simples para validar formato de email
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void limparCampos() {
        nomeField.setText("");
        emailField.setText("");
        senhaField.setText("");
    }

    private void voltarParaLogin() {
        this.dispose();  // Fecha a tela de cadastro
        new LoginFrame().setVisible(true);  // Abre a tela de login
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CadastroUsuarioFrame().setVisible(true);
        });
    }
}
