package com.now.naaga.game.exception;

public class GameNotArrivalException extends GameException{
    
    public GameNotArrivalException(GameExceptionType gameExceptionType) {
        super(gameExceptionType);
    }
}
