import java.util.Arrays;
import java.util.Scanner;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;


import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class Menu {

	static Scanner entrada = new Scanner(System.in);
	
	
	public static MongoCollection<Document> conectar() {
		try {
			
			MongoClient conexao = MongoClients.create(
				MongoClientSettings.builder().applyToClusterSettings(builder -> 
						builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017)))
					).build()
			);
			
			MongoDatabase database = conexao.getDatabase("exercicio-CRUD");
			MongoCollection<Document> colecao = database.getCollection("produtos");
			return colecao;
		}
		
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void listar() {
		MongoCollection<Document> colecao = conectar();
		
		if(colecao.countDocuments() > 0) {
			MongoCursor<Document> cursor = colecao.find().iterator();
			
			try {
				System.out.println("Listando Produtos...");
				System.out.println("------------------------------------------------");
				
				while(cursor.hasNext()) {
					String json = cursor.next().toJson();
					JSONObject obj = new JSONObject(json);
					JSONObject id = obj.getJSONObject("_id");
					System.out.println("ID: " + id.get("$oid"));
					System.out.println("Produto: " + obj.get("nome"));
					System.out.println("Preco: R$" + obj.get("preco"));
					System.out.println("Estoque: " + obj.get("estoque"));
					System.out.println();
				}
			}
			
			catch(Exception e) {
				e.printStackTrace();
			}
			
			finally{
				cursor.close();
			}
		}
		
		else {
			System.out.println("Nao existem documentos cadastrados");
		}
		
	}
	
	public static void inserir() {
		MongoCollection<Document> colecao = conectar();
		System.out.println("Nome: ");
		String nome = entrada.nextLine();
		
		System.out.println("Preco: ");
		double preco = Double.parseDouble(entrada.nextLine());
		
		System.out.println("Estoque: ");
		int estoque = Integer.parseInt(entrada.nextLine());
		
		JSONObject produto = new JSONObject();
		produto.put("nome", nome);
		produto.put("preco", preco);
		produto.put("estoque", estoque);
		
		colecao.insertOne(Document.parse(produto.toString()));
		System.out.println("Produto inserido com sucesso");
	}
	
	
	public static void atualizar() {
		MongoCollection<Document> colecao = conectar();
		
		System.out.println("ID: ");
		String _id = entrada.nextLine();
		
		System.out.println("Nome: ");
		String nome = entrada.nextLine();
		
		System.out.println("Preco: ");
		double preco = Double.parseDouble(entrada.nextLine());
		
		System.out.println("Estoque: ");
		int estoque = Integer.parseInt(entrada.nextLine());
		
		Bson query = combine(set("nome", nome), set("preco", preco), set("estoque", estoque));
		UpdateResult res = colecao.updateOne(new Document("_id", new ObjectId(_id)), query);
		
		if(res.getModifiedCount() == 1) {
			System.out.println("Produto atualizado com sucesso!");
		}
		
	}
	
	public static void excluir() {
		
		MongoCollection<Document> colecao = conectar();
		
		System.out.println("ID: ");
		String _id = entrada.nextLine();
		
		DeleteResult res = colecao.deleteOne(new Document("_id", new ObjectId(_id)));
		
		if(res.getDeletedCount() == 1) {
			System.out.println("O Produto foi excluido com Sucesso!");
		}
		else {
			System.out.println("Nao foi possivel excluir o produto");
		}
		
	}
	
	public static void exibirOpcoes() {
		System.out.println("==================Gerenciamento de Produtos===============");
		System.out.println("Selecione uma opção: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Excluir produtos.");
		
		int opcao = Integer.parseInt(entrada.nextLine());
		
		if(opcao == 1) {
			listar();
		}
		else if(opcao == 2) {
			inserir();
		}
		else if(opcao == 3) {
			atualizar();
		}
		else if(opcao == 4) {
			excluir();
		}
		else {
			System.out.println("Opção inválida.");
		}
	}
	
	
}
