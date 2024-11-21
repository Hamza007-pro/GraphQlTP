package ma.projet.graph.controllers;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.entities.TransactionInput;
import ma.projet.graph.entities.TypeTransaction;
import ma.projet.graph.repositories.CompteRepository;
import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class TransactionControllerGraphQL {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CompteRepository compteRepository;

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionInput transactionInput) {
        Compte compte = compteRepository.findById(transactionInput.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        Transaction t1 = new Transaction();
        t1.setType(transactionInput.getType());
        t1.setMontant(transactionInput.getMontant());
        t1.setDate(LocalDate.now());
        t1.setCompte(compte);

        transactionRepository.save(t1);

        compte.getTransactions().add(t1);
        double newSolde = compte.getTransactions().stream()
                .mapToDouble(t -> t.getType() == TypeTransaction.DEPOT ? t.getMontant() : -t.getMontant())
                .sum();
        compte.setSolde(newSolde);
        compteRepository.save(compte);

        return t1;
    }

    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        return transactionRepository.findByCompte(compte);
    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count();
        double sumDeposits = transactionRepository.sumByType(TypeTransaction.DEPOT) != null
                ? transactionRepository.sumByType(TypeTransaction.DEPOT)
                : 0.0;
        double sumWithdrawals = transactionRepository.sumByType(TypeTransaction.RETRAIT) != null
                ? transactionRepository.sumByType(TypeTransaction.RETRAIT)
                : 0.0;

        return Map.of(
                "count", count,
                "sumDepots", sumDeposits,
                "sumRetraits", sumWithdrawals
        );
    }


    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }
}