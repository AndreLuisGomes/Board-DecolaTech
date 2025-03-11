package br.com.dio.ui;

import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectioConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class BoardMenu {

    private final BoardEntity entity;
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute(){
        try{
            System.out.printf("Bem vindo ao board %s, escolha a opção desejada:\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um coard");
                System.out.println("6 - Ver colunas");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver cards");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para menu anterior...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu!");
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
                System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna:");
        var cardId = scanner.nextLong();

    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException{
        try(
                var connection = getConnection()
                ){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s} tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        }
    }

    private void showColumn() throws SQLException{
        System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while(!columnsIds.contains(selectedColumn)){
            System.out.println();
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s -  %s\nDescrição: %s", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }

    }

    private void showCard() throws SQLException{
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId).ifPresentOrElse(c -> {
                System.out.printf("Card %s - %S.\n", c.id(), c.title());
                System.out.printf("Descrição: %s\n", c.description());
                System.out.println(c.blocked() ? "Está bloqueado. Motivo: " + c.blockReason() : "Não está bloqueado");
                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
            }, () -> System.out.printf("Não existe card com o id %s\n", selectedCardId));
        }
    }
}
