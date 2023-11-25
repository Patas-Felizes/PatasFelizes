/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import main.model.Voluntario;
import main.repositories.VoluntarioRepository;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author micha
 */
public class VoluntarioService {
    
    private VoluntarioRepository voluntarioRepository;
    
    public VoluntarioService(){
         voluntarioRepository = new VoluntarioRepository();
    }

    public void Salvar(int idVoluntario, String nome, String email, String telefone, byte[] fotoVoluntario) {
        try {
            voluntarioRepository.Salvar(idVoluntario, nome, email, telefone, fotoVoluntario);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Voluntario> ObterVoluntarios() {
        return voluntarioRepository.ObterVoluntarios();
    }
    
    public List<Voluntario> ObterVoluntarios(String nome) {
        return voluntarioRepository.ObterVoluntariosPorNome(nome);
    }
    
    public Set<String> ObterNomeVoluntarios() {
        return voluntarioRepository.EncontrarNomesVoluntarios();
    }

    public Voluntario ObterVoluntarioPorNome(String voluntarioString) {
        return voluntarioRepository.EncontrarVoluntarioPor(voluntarioString);
    }
     
}
