package ui;

import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    private String nomeUsuario;

    public MenuFrame(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;

        setTitle("Menu Principal - Sistema Financeiro");
        setSize(600, 200);  // Ajustando o tamanho da janela para algo mais compacto
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Layout principal com BorderLayout
        setLayout(new BorderLayout());

        // Cabeçalho com o nome do usuário
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);  // Fundo branco
        JLabel welcomeLabel = new JLabel("Bem-vindo, " + nomeUsuario);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.BLACK);  // Texto preto
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Painel principal branco
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));  // Alinhamento horizontal dos botões
        mainPanel.setBackground(Color.WHITE);  // Fundo branco

        // Botões do menu
        JButton btnCategorias = new JButton("Categorias");
        JButton btnTransacoes = new JButton("Transações");
        JButton btnHistorico = new JButton("Histórico");
        JButton btnResumo = new JButton("Resumo");

        // Estilos dos botões
        estiloBotao(btnCategorias);
        estiloBotao(btnTransacoes);
        estiloBotao(btnHistorico);
        estiloBotao(btnResumo);

        // Adicionando os botões ao painel
        mainPanel.add(btnCategorias);
        mainPanel.add(btnTransacoes);
        mainPanel.add(btnHistorico);
        mainPanel.add(btnResumo);

        add(mainPanel, BorderLayout.CENTER);  // Adiciona o painel principal (com botões) ao centro da tela

        // Definindo a ação dos botões
        btnCategorias.addActionListener(e -> abrirMainFrame(0)); // Chama a aba "Categorias"
        btnTransacoes.addActionListener(e -> abrirMainFrame(1)); // Chama a aba "Transações"
        btnHistorico.addActionListener(e -> abrirMainFrame(2)); // Chama a aba "Histórico"
        btnResumo.addActionListener(e -> abrirMainFrame(3)); // Chama a aba "Resumo"
    }

    private void estiloBotao(JButton botao) {
        // Botão simples com fundo transparente e texto preto
        botao.setFont(new Font("Arial", Font.PLAIN, 14));
        botao.setBackground(Color.WHITE);  // Fundo transparente (sem cor)
        botao.setForeground(Color.BLACK);  // Texto preto
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // Borda preta simples
        botao.setPreferredSize(new Dimension(120, 40));  // Botões pequenos e compactos
    }

    private void abrirMainFrame(int abaIndex) {
        // Criando o usuário logado (aqui você pode pegar o usuário logado real)
        Usuario usuarioLogado = new Usuario("teste@exemplo.com", "123456", "Felipe");

        // Passando o índice da aba e o usuário logado para o MainFrame
        MainFrame main = new MainFrame(abaIndex, usuarioLogado); // Passando o usuário logado
        main.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuFrame("Felipe").setVisible(true);
        });
    }
}
