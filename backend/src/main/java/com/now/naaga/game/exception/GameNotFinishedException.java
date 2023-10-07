package com.now.naaga.game.exception;

public class GameNotFinishedException extends GameException{
    
    public GameNotFinishedException(GameExceptionType gameExceptionType) {
        super(gameExceptionType);
    }
}
