package com.palagi.demo_park_api.service;


import com.palagi.demo_park_api.entity.Cliente;
import com.palagi.demo_park_api.exception.CpfUniqueViolationException;
import com.palagi.demo_park_api.exception.EntityNotFoundException;
import com.palagi.demo_park_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente) {
        try {
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF '%s' nao pode ser cadastrado, Ja existe no sistema", cliente.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente id=%s nao encontrado.", id))
        );

    }
}
