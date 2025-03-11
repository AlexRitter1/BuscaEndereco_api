package com.alex.buscacep.service;

import com.alex.buscacep.config.ConsumoViaCep;
import com.alex.buscacep.dto.BuscaDTO;
import com.alex.buscacep.dto.BuscaEnderecoResponseDTO;
import com.alex.buscacep.dto.EnderecoDTO;
import com.alex.buscacep.entity.Busca;
import com.alex.buscacep.entity.Endereco;
import com.alex.buscacep.repository.BuscaRepository;
import com.alex.buscacep.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    private ConsumoViaCep client;

    public EnderecoService (ConsumoViaCep client){
        this.client = client;
    }

    @Autowired
    private BuscaRepository buscaRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public EnderecoDTO buscaEndereco (String cep) throws IOException, InterruptedException {

        // var endereco = enderecoRepository.findByCep(cep);
        // if (endereco.isPresent()) { return new EnderecoDTO(endereco); }

        var enderecoDTO = conexaoViaCep(cep);
        var enderecoNovo = new Endereco(enderecoDTO);
        enderecoRepository.save(enderecoNovo);

        Busca busca = new Busca();
        busca.setDataHoraBusca(LocalDateTime.now());
        busca.setEndereco(enderecoNovo);
        buscaRepository.save(busca);

        return enderecoDTO;
    }

    public EnderecoDTO conexaoViaCep(String cep) throws IOException, InterruptedException {
        return client.conexaoViaCep(cep);
    }

    public List<BuscaEnderecoResponseDTO> findAll(){
        List<Busca> buscas = buscaRepository.findAll();
        List<BuscaEnderecoResponseDTO> dtoResponse = buscas.stream()
                .map(BuscaEnderecoResponseDTO::new)
                .collect(Collectors.toList());
        return dtoResponse;
    }
}