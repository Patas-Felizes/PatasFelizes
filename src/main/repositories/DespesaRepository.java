package main.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.*;
import main.model.Animal;
import main.model.Despesa;
import main.model.FiltroDespesa;
import static main.utils.DateHelper.DateToCalendar;
import static main.utils.DateHelper.invalidString;

public class DespesaRepository extends BaseRepository<Despesa> {

    public DespesaRepository() {
        super(Despesa.class);
    }


    public Despesa Salvar(int idDespesa, String descricao, Double valor, Calendar data, String tipo, boolean realizado, byte[] fotoComprovante) throws IllegalAccessException, SQLException{
        Despesa despesa;

        if (idDespesa == -1) {
            despesa = new Despesa();
            despesa.setDescricao(descricao);
            despesa.setData(data);
            despesa.setValor(valor);
            despesa.setTipo(tipo);
            despesa.setRealizada(realizado);
            despesa.setFotoComprovante(fotoComprovante);
            idDespesa = Inserir(despesa);
            despesa.setId(idDespesa);
        } else {
            despesa = EncontrarDespesaPor(idDespesa);
            despesa.setDescricao(descricao);
            despesa.setData(data);
            despesa.setValor(valor);
            despesa.setTipo(tipo);
            despesa.setFotoComprovante(fotoComprovante);
            despesa.setRealizada(realizado);
            atualizarDespesa(despesa);
        }

        return despesa;
    }
    
    private Despesa atualizarDespesa(Despesa despesa) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(
                 "UPDATE Despesas SET descricao=?, valor=?, data=?, tipo=?, realizada=?, fotoComprovante=? WHERE id=?")) {

        statement.setString(1, despesa.getDescricao());
        statement.setDouble(2, despesa.getValor());
        statement.setTimestamp(3, new Timestamp(despesa.getData().getTimeInMillis()));
        statement.setString(4, despesa.getTipo());
        statement.setBoolean(5, despesa.isRealizada());
        statement.setBytes(6, despesa.getFotoComprovante());
        statement.setInt(7, despesa.getId());

        System.out.println("SQL gerado: " + statement.toString());

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            return despesa;
        }
    } catch (SQLException e) {
        throw e;
    }

    return null;
}


    public List<Despesa> ObterDespesas() {
        return new ArrayList<>(SelecionarTodos("*", null,"Data desc", Despesa.class));
    }

    public Set<String> ObterTiposDespesa() {
        return new HashSet<>( SelecionarTodos("TIPO", null,null, String.class));
    }

    public void Deletar(Despesa despesa) {
        //Excluir(despesa);
    }

    public Despesa EncontrarDespesaPor(int id) {
        String sql = "SELECT * FROM Despesas WHERE Id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Mapear os resultados para o objeto Despesa
                    return mapearDespesa(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Método para mapear uma despesa a partir de um ResultSet
    private Despesa mapearDespesa(ResultSet resultSet) throws SQLException {
        Despesa despesa = new Despesa();
        despesa.setId(resultSet.getInt("Id"));
        despesa.setDescricao(resultSet.getString("Descricao"));
        despesa.setValor(resultSet.getDouble("Valor"));
        despesa.setData(DateToCalendar(resultSet.getDate("Data")));
        despesa.setTipo(resultSet.getString("Tipo"));
        despesa.setRealizada(resultSet.getBoolean("Realizada"));
        despesa.setDataCadastro(DateToCalendar(resultSet.getDate("DataCadastro")));
        despesa.setFotoComprovante(resultSet.getBytes("FotoComprovante"));

        return despesa;
    }

    public List<Despesa> ObterDespesasPorDescricao(String desc) {
        return SelecionarTodos("*", "DESCRICAO LIKE '%"+desc+"%'", null, Despesa.class);
    }

    public List<Despesa> FiltrarDespesas(FiltroDespesa filtro, int idAnimal) throws SQLException {

        List<Despesa> despesas = new ArrayList<Despesa>();     

        StringBuilder sql = new StringBuilder("SELECT D.* FROM DESPESAS D ");
        sql.append("LEFT JOIN PROCEDIMENTOS P ON D.ID = P.IDDESPESA ");
        sql.append("WHERE 1 = 1");

        if (!invalidString(filtro.getAnimal())) {
            sql.append(" AND P.IDANIMAL = ").append(idAnimal);
        }       

        if(!invalidString(filtro.getTipo())){
            sql.append(" AND D.TIPO = '").append(filtro.getTipo()).append("'");
        }

        if (filtro.getDataFinal() != null && filtro.getDataInicial() != null) {
            sql.append(" AND D.Data BETWEEN ? AND ?");
        }

        if (!invalidString(filtro.getOrdenacao())){
            sql.append(" ORDER BY D.").append(filtro.getOrdenacao());
        }

        System.out.println(sql);

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            if(filtro.getDataFinal() != null && filtro.getDataInicial() != null){
                statement.setTimestamp(1, new Timestamp(filtro.getDataInicial().getTimeInMillis()));
                statement.setTimestamp(2, new Timestamp(filtro.getDataFinal().getTimeInMillis()));                        
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Despesa despesa = mapearDespesa(resultSet);
                    despesas.add(despesa);
                }
            }
        } catch (SQLException e) {
            throw e;
        }

        return despesas;
    }

}
