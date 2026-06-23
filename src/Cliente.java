public class Cliente {

    private static int ultimoID = 10_000;

    private String nome;
    private int documento;

    public Cliente(String nome) {

        documento = ultimoID++;
        setNome(nome);

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {

        if (nome.split(" ").length < 2) {
            throw new IllegalArgumentException("Nome deve conter no mínimo duas palavras");
        }
        this.nome = nome;

    }

    @Override
    public String toString() {
        return nome + " (" + documento + ")";
    }

    @Override
    public int hashCode() {
        return documento;
    }
}
