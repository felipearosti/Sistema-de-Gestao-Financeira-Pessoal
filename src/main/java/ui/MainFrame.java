package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Gestão Financeira");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelCategorias = new CategoriaPanel();
        JPanel panelTransacoes = new TransacaoPanel();
        JPanel panelHistorico = new HistoricoPanel();
        JPanel panelResumo = new ResumoPanel();

        tabbedPane.addTab("Categorias", panelCategorias);
        tabbedPane.addTab("Transações", panelTransacoes);
        tabbedPane.addTab("Histórico", panelHistorico);
        tabbedPane.addTab("Resumo", panelResumo);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
