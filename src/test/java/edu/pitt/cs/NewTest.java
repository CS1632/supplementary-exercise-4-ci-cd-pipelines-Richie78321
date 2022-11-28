package edu.pitt.cs;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.mockito.*;

public class NewTest {
    public enum Hand {
        ROCK,
        PAPER,
        SCISSORS
    }
    
    public class RockPaperScissors {
        private Player p1;
        private Player p2;
        private ScoreBoard sb;
    
        public RockPaperScissors(Player p1, Player p2, ScoreBoard sb) {
            this.p1 = p1;
            this.p2 = p2;
            this.sb = sb;
        }
    
        private void round() {
            Hand p1Hand = p1.throwHand();
            Hand p2Hand = p2.throwHand();
            if (p1Hand == p2Hand) {
                sb.draw();
            } else if (p1Hand == Hand.ROCK && p2Hand == Hand.SCISSORS) {
                sb.p1Win();
            } else if (p1Hand == Hand.PAPER && p2Hand == Hand.ROCK) {
                sb.p1Win();
            } else if (p1Hand == Hand.SCISSORS && p2Hand == Hand.PAPER) {
                sb.p1Win();
            } else {
                sb.p2Win();
            }
        }
        
        public String play(int rounds) {
            for (int i = 0; i < rounds; i++) {
                round();
            }
            return sb.toString();
        }
    }
    
    public class ScoreBoard {
        private int p1Wins = 0;
        private int p2Wins = 0;
        private int draws = 0;
        
        public void p1Win() { p1Wins++; }
        public void p2Win() { p2Wins++; }
        public void draw() { draws++; }
        public String toString() {
            return "(" + p1Wins + ":" + p2Wins + ":" + draws + ")";
        }
    }
    
    public interface Player {	
        public Hand throwHand();
    }
    
    public class RockPlayer implements Player {
        public Hand throwHand() { return Hand.ROCK; }
    }
    
    public class PaperPlayer implements Player {
        public Hand throwHand() { return Hand.PAPER; }
    }
    
    public class ScissorsPlayer implements Player {
        public Hand throwHand() { return Hand.SCISSORS; }
    }
    
    public class RoundRobinPlayer implements Player {
        private int i = 0;
        public Hand throwHand() {
            // Returns rock, paper, scissors in round robin order
            Hand ret = Hand.values()[i];
            i = ++i % 3;
            return ret;
        }
    }

    /**
     * Preconditions: Create a PaperPlayer p1.
     *                Create a RockPlayer p2.
     *                Create a ScoreBoard sb.
     *                Create a RockPaperScissors game rps using p1, p2, sb.
     * Execution steps: Call rps.play(5).
     * Postconditions: 5 p1 wins, 0 p2 wins, and 0 draws are posted on ScoreBoard sb.
     */
    @Test
    public void testPapervsRock5() {
        PaperPlayer p1 = Mockito.mock(PaperPlayer.class);
        Mockito.when(p1.throwHand()).thenReturn(Hand.PAPER);
        RockPlayer p2 = Mockito.mock(RockPlayer.class);
        Mockito.when(p2.throwHand()).thenReturn(Hand.ROCK);
        ScoreBoard sb = Mockito.mock(ScoreBoard.class);
        RockPaperScissors rps = new RockPaperScissors(p1, p2, sb);

        rps.play(5);

        Mockito.verify(sb, Mockito.times(5)).p1Win();
        Mockito.verify(sb, Mockito.never()).p2Win();
        Mockito.verify(sb, Mockito.never()).draw();
    }

    /**
     * Preconditions: Create a PaperPlayer p1.
     *                Create a RoundRobinPlayer p2.
     *                Create a ScoreBoard sb.
     *                Create a RockPaperScissors game rps using p1, p2, sb.
     * Execution steps: Call rps.play(3).
     * Postconditions: 1 p1 win, 1 p2 win, and 1 draw are posted on ScoreBoard sb.
     */
    @Test
    public void testPapervsRoundRobin3() {
        PaperPlayer p1 = Mockito.mock(PaperPlayer.class);
        Mockito.when(p1.throwHand()).thenReturn(Hand.PAPER);
        RoundRobinPlayer p2 = Mockito.mock(RoundRobinPlayer.class);
        Mockito.doAnswer(
            AdditionalAnswers.returnsElementsOf(Arrays.asList(new Hand[] { Hand.ROCK, Hand.PAPER, Hand.SCISSORS }))
        ).when(p2).throwHand();
        ScoreBoard sb = Mockito.mock(ScoreBoard.class);
        RockPaperScissors rps = new RockPaperScissors(p1, p2, sb);

        rps.play(3);

        Mockito.verify(sb, Mockito.times(1)).p1Win();
        Mockito.verify(sb, Mockito.times(1)).p2Win();
        Mockito.verify(sb, Mockito.times(1)).draw();
    }
}
