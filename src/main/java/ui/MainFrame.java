package ui;

import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    // Construtor com índice da aba inicial e o usuário logado
    public MainFrame(int abaInicial, Usuario usuarioLogado) {
        setTitle("Sistema de Gestão Financeira");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI(usuarioLogado);  // Passando o usuário logado para os painéis

        tabbedPane.setSelectedIndex(abaInicial);
    }

    private void initUI(Usuario usuarioLogado) {
        tabbedPane = new JTabbedPane();

        // Passando o usuário logado para os painéis
        JPanel panelCategorias = new CategoriaPanel();
        JPanel panelTransacoes = new TransacaoPanel(usuarioLogado); // Passando o usuário logado
        JPanel panelHistorico = new HistoricoPanel(usuarioLogado); // Passando o usuário logado
        JPanel panelResumo = new ResumoPanel(usuarioLogado); // Passando o usuário logado

        tabbedPane.addTab("Categorias", panelCategorias);
        tabbedPane.addTab("Transações", panelTransacoes);
        tabbedPane.addTab("Histórico", panelHistorico);
        tabbedPane.addTab("Resumo", panelResumo);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
