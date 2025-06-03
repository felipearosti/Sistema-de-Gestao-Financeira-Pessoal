package util;

import dao.GenericDAO;
import model.Usuario;

public class CriarUsuarioTeste {
    public static void main(String[] args) {
        GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);

        Usuario user = new Usuario("teste@exemplo.com", "123456", "Felipe");

        usuarioDAO.salvar(user);

        System.out.println("Usuário criado com sucesso!");
        HibernateUtil.shutdown(); // Fecha a fábrica de sessões
    }
}
