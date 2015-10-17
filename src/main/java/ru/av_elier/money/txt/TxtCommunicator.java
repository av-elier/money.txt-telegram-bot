package ru.av_elier.money.txt;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Adelier on 16.10.2015.
 */
public abstract class TxtCommunicator {

    public abstract void addLine(MoneyTxtLine line);

    public abstract List<MoneyTxtLine> getLastLines(int limit);
    public Optional<MoneyTxtLine> getLastLine() {
        List<MoneyTxtLine> lastLines = getLastLines(1);
        if (lastLines.isEmpty())
            return Optional.empty();
        else
            return Optional.of(lastLines.get(0));
    }
}
