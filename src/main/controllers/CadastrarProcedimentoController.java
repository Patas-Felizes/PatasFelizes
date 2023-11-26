/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.App;
import main.interfaces.InicializadorComDado;
import main.interfaces.InicializadorComOrigemEDado;
import main.model.Procedimento;
import main.services.ProcedimentoService;
import main.services.VoluntarioService;
import static main.utils.Constantes.FORM_ANIMAL_DETALHES;
import main.utils.RealFormatter;
import main.utils.TextFieldUtils;
import org.controlsfx.control.textfield.TextFields;
/**
 *
 * @author micha
 */
public class CadastrarProcedimentoController extends CustomController implements InicializadorComDado{
    @FXML
    private DatePicker dataProcedimento;
   
    @FXML
    private CheckBox checkBoxJaRealizado;
    
    @FXML
    private TextArea descricaoProcedimento;

    @FXML
    private Button salvarProcedimento;

    @FXML
    private Button cancelarCadastro;

    @FXML
    private TextField tipoProcedimento;
    
    @FXML
    private TextField valorProcedimento;

    @FXML
    private TextField voluntarioProcedimento;
    
    private ProcedimentoService procedimentoService;
    private VoluntarioService voluntarioService;
    private int idAnimal;
    private Procedimento procedimento;
    private boolean jaRealizado;
    
    @Override
    public void Inicializar(Pane contentFather, Stage primmaryStage, Pane blackShadow, Object[] dados) {
        idAnimal = ObterDadoArray(dados, 0) == null ? -1 : (int) ObterDadoArray(dados, 0) ;
        procedimento = ObterDadoArray(dados,1) == null ? null : (Procedimento) ObterDadoArray(dados, 1);
                
        initialize();
        setListeners(contentFather, primmaryStage, blackShadow);     
    }
   
    public void configurarVoluntarios(){
       Set<String> voluntariosPossiveis = voluntarioService.ObterNomeVoluntarios();
       TextFields.bindAutoCompletion(voluntarioProcedimento, voluntariosPossiveis);   
    }
    
    public void configurarTiposProcedimento(){
       Set<String> tiposPossiveis = procedimentoService.ObterNomesTiposProcedimento();
       TextFields.bindAutoCompletion(tipoProcedimento, tiposPossiveis);   
    }
     
    public void initialize(){
        procedimentoService = new ProcedimentoService();
        voluntarioService = new VoluntarioService();
        configurarVoluntarios();
        configurarTiposProcedimento();
        TextFieldUtils.setupCurrencyTextField(valorProcedimento);

        if(procedimento != null) setData();
    }
    
    public void setListeners(Pane contentFather, Stage primmaryStage, Pane blackShadow){
        salvarProcedimento.setOnMouseClicked(e->{
            cadastrarNovoProcedimento(primmaryStage);
            App.getInstance().EntrarTelaOnResume(FORM_ANIMAL_DETALHES ,contentFather, primmaryStage, blackShadow, null);                       
        });
        
        cancelarCadastro.setOnMouseClicked(e ->{
            App.getInstance().FecharDialog(primmaryStage, blackShadow);                       
        });

        checkBoxJaRealizado.setOnAction(event -> {
            jaRealizado = checkBoxJaRealizado.isSelected();
        });
        
        dataProcedimento.setOnAction(e -> {
            LocalDate data = dataProcedimento.getValue();
            if (data != null && data.isEqual(LocalDate.now())) {
                checkBoxJaRealizado.setVisible(true);
            } else {
                checkBoxJaRealizado.setVisible(false);
            }
        });     
    }
    
    public Procedimento cadastrarNovoProcedimento(Stage primaryStage) {
        String descricao = descricaoProcedimento.getText();
        LocalDate data = dataProcedimento.getValue();
        String tipo = tipoProcedimento.getText();
        String voluntario = voluntarioProcedimento.getText();
        double valor = RealFormatter.unformatarReal(valorProcedimento.getText());

        return procedimentoService.Salvar(procedimento == null ? -1 : procedimento.getId(), descricao, data, tipo, valor, voluntario, idAnimal, jaRealizado);      
    }

    private void setData() {
        descricaoProcedimento.setText(procedimento.getDescricao());
        LocalDate localDate = procedimento.getData().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        dataProcedimento.setValue(localDate);
        tipoProcedimento.setText(procedimento.getTipo());
        voluntarioProcedimento.setText(procedimento.getVoluntario() == null ? "" : procedimento.getVoluntario().getNome());
        valorProcedimento.setText(RealFormatter.formatarComoReal(procedimento.getDespesa() == null ? 0 : procedimento.getDespesa().getValor()));
        jaRealizado = procedimento.isRealizado();
    }

}
