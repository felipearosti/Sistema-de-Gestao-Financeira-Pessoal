package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
public class Transacao {

    public enum TipoTransacao {
        RECEITA,
        DESPESA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Transacao() {}

    public Transacao(Double valor, LocalDate data, String descricao, TipoTransacao tipo, Categoria categoria, Usuario usuario) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
        this.usuario = usuario;
    }

    // Getters e setters

    public Long getId() { return id; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
