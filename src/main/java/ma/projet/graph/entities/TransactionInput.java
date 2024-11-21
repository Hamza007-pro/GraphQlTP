package ma.projet.graph.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInput {

    private TypeTransaction type; // Type de transaction : DEPOT ou RETRAIT
    private Double montant;       // Montant de la transaction
    private Long compteId;
}
