import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pedido implements Comparable<Pedido> {

	private static int ultimoID = 1;

	private static final double DESCONTO_PG_A_VISTA = 0.15;
	private int idPedido;
	private Lista<ItemDePedido> itensDePedido;
	private LocalDate dataPedido;
	private int quantItensDePedido = 0;
	private int formaDePagamento;
	private Cliente cliente;

	public Pedido(LocalDate dataPedido, int formaDePagamento, Cliente cliente) {

		idPedido = ultimoID++;
		itensDePedido = new Lista<>();
		quantItensDePedido = 0;
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
		this.cliente = cliente;
	}

	public Lista<ItemDePedido> getItensDoPedido() {
		return itensDePedido;
	}

	public ItemDePedido existeNoPedido(Produto produto) {

		ItemDePedido procurado = new ItemDePedido(produto, 0, 0.1);
		return itensDePedido.buscarPor(
				(item1, item2) -> (item1.getProduto().hashCode() - item2.getProduto().hashCode()), procurado);
	}

	public boolean incluirProduto(Produto novo, int quantidade) {

		ItemDePedido itemDePedido = existeNoPedido(novo);

		if (itemDePedido != null) {
			itemDePedido.setQuantidade(quantidade + itemDePedido.getQuantidade());
		} else {
			itensDePedido.inserir(new ItemDePedido(novo, quantidade, novo.valorDeVenda()), quantItensDePedido);
			quantItensDePedido++;
		}
		return true;
	}

	public double valorFinal() {

		double valorPedido = 0;
		BigDecimal valorPedidoBD;

		valorPedido = itensDePedido.somarMultiplicacoes((item -> item.getPrecoVenda()), (item -> item.getQuantidade()));

		if (formaDePagamento == 1) {
			valorPedido = valorPedido * (1.0 - DESCONTO_PG_A_VISTA);
		}

		valorPedidoBD = new BigDecimal(Double.toString(valorPedido));

		valorPedidoBD = valorPedidoBD.setScale(2, RoundingMode.HALF_UP);

		return valorPedidoBD.doubleValue();
	}

	@Override
	public String toString() {

		StringBuilder stringPedido = new StringBuilder();

		stringPedido.append("==============================\n");
		stringPedido.append("ID do pedido: " + idPedido + "\n");

		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		stringPedido.append("Data do pedido: " + formatoData.format(dataPedido) + "\n");
		stringPedido.append("Cliente do pedido: " + cliente + "\n");

		stringPedido.append("Pedido com " + quantItensDePedido + " itens.\n");
		stringPedido.append("Itens de pedido no pedido:\n");
		stringPedido.append(itensDePedido.toString() + "\n");

		stringPedido.append("Pedido pago ");
		if (formaDePagamento == 1) {
			stringPedido.append(
					"à vista. Percentual de desconto: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%\n");
		} else {
			stringPedido.append("parcelado.\n");
		}

		stringPedido.append("Valor total do pedido: R$ " + String.format("%.2f", valorFinal()));

		return stringPedido.toString();
	}

	public int hashCode() {
		return idPedido;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}
		if ((obj == null) || (!(obj instanceof Pedido))) {
			return false;
		}

		Pedido outro = (Pedido) obj;
		return this.hashCode() == outro.hashCode();
	}

	@Override
	public int compareTo(Pedido outroPedido) {

		return (this.hashCode() - outroPedido.hashCode());
	}
}