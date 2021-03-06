package jeopardy.game.ui.display;

import jeopardy.Config;
import jeopardy.game.Game;
import jeopardy.game.Round;
import jeopardy.game.bot.BotType;
import jeopardy.game.bot.discord.DiscordBot;
import jeopardy.game.utils.Colors;
import jeopardy.game.utils.Utils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by XLIII on 2015-12-21.
 */
public class QuestionSelectionPanel extends JPanel {

    private Game game;

    private int counter;

    private List<JLabel> themes = new ArrayList<>();
    private List<JButton> questions = new ArrayList<>();

    public QuestionSelectionPanel(Game game) throws HeadlessException {
        this.game = game;
        init();
    }

    private void init() {
        setLayout(new GridLayout(Config.ROUND_PER_THEME + 1, Config.THEMES_PER_SECTION));
        setSize(950, 500);
        setVisuals();

        setVisible(true);
    }

    public boolean sectionEnded() {
        return counter == 0;
    }

    public void startSection(Map<String, List<Round>> rounds) {
        themes.clear();
        questions.clear();
        removeAll();
        for (String theme : rounds.keySet()) {
            add(createThemeLabel(theme));
        }

        int index = 0;
        while (true) {
            for (List<Round> theme : rounds.values()) {
                if (theme.size() <= index)  {
                    return;
                }

                add(createRoundButton(theme.get(index)));
            }
            index++;
        }
    }

    private void setVisuals() {
        setBackground(Color.black);
    }

    public JButton createRoundButton(final Round round) {
        final JButton button = new JButton(round.getScore() + "");
        button.setFont(Utils.getJeopardyFont(Font.BOLD, 50));
        button.setBackground(Colors.backgroundBlue);
        button.setBorder(new LineBorder(Color.black, 4));
        button.setForeground(Colors.roundYellow);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            button.setEnabled(false);
            button.setText("");
            counter--;

            game.onRoundChosen(round);
        });
        counter++;
        questions.add(button);
        return button;
    }

    public JLabel createThemeLabel(String theme) {
        JLabel label = new JLabel(Utils.wrapAndCenter(theme), JLabel.CENTER);
        label.setFont(new Font("Century", Font.BOLD, 10));
        label.setFont(Utils.getJeopardyFont(Font.PLAIN, 30));
        label.setBackground(Colors.backgroundBlue);
        label.setBorder(new LineBorder(Color.black, 4));
        label.setForeground(Colors.themeWhite);
        label.setOpaque(true);
        themes.add(label);
        return label;
    }

    private final int PADDING = 12;

    public String printSelectionPanel() {
        StringBuilder sb = new StringBuilder();
        Iterator<JLabel> it = themes.iterator();
        while (it.hasNext()) {
            sb.append(StringUtils.center(Utils.unwrap(it.next().getText()), PADDING));
            if (it.hasNext()) {
                sb.append("|");
            }
        }
        sb.append("\n");
        sb.append(StringUtils.repeat("-", themes.size() * (PADDING + 1)));
        sb.append("\n");

        for (int i = 0; i < questions.size(); i++) {
            sb.append(StringUtils.center(questions.get(i).getText(), PADDING));
            if ((i + 1) % Config.THEMES_PER_SECTION == 0) {
                sb.append("\n");
            } else {
                sb.append("|");
            }
        }
        if (Config.BOT == BotType.DISCORD) {
            //Force code block mode
            return DiscordBot.TAG_CODE_BLOCK + sb.toString() + DiscordBot.TAG_CODE_BLOCK;
        }
        return sb.toString();
    }
}
