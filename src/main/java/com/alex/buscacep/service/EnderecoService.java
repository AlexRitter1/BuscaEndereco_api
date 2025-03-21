package com.alex.buscacep.service;

import com.alex.buscacep.domain.BuscaEnderecoResponseDTO;
import com.alex.buscacep.domain.endereco.EnderecoRequestDTO;
import com.alex.buscacep.domain.busca.Busca;
import com.alex.buscacep.domain.endereco.Endereco;
import com.alex.buscacep.repository.BuscaRepository;
import com.alex.buscacep.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    @Autowired
    private CepService client;

    @Autowired
    private BuscaRepository buscaRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private SalvarBuscaService salvarBuscaService;

    public BuscaEnderecoResponseDTO buscaEndereco (String cep) throws IOException, InterruptedException {

        Optional<Endereco> enderecoDb = enderecoRepository.findByCep(cep.substring(0, 5) + "-" + cep.substring(5));
        if (enderecoDb.isPresent()) {
            return new BuscaEnderecoResponseDTO(salvarBuscaService.salvarBusca(enderecoDb.get()));
        }

        var enderecoDTO = conexaoViaCep(cep);
        Endereco enderecoNovo = new Endereco(enderecoDTO);
        enderecoRepository.save(enderecoNovo);
        return new BuscaEnderecoResponseDTO(salvarBuscaService.salvarBusca(enderecoNovo));
    }

    public List<BuscaEnderecoResponseDTO> findAll(){
        List<Busca> buscas = buscaRepository.findAll();
        List<BuscaEnderecoResponseDTO> dtoResponse = buscas.stream()
                .map(BuscaEnderecoResponseDTO::new)
                .collect(Collectors.toList());
        return dtoResponse;
    }

    public EnderecoRequestDTO conexaoViaCep(String cep) throws IOException, InterruptedException {
        return client.viaCep(cep);
    }
}