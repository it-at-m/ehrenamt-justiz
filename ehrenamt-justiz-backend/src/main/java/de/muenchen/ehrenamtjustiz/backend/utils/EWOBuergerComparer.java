package de.muenchen.ehrenamtjustiz.backend.utils;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class EWOBuergerComparer {

    private EWOBuergerComparer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get attributes with conflict. Compare with EWO-data
     *
     * @param currentBuerger Daten aus EJ
     * @param newBuerger Daten aus EWO
     * @return Liste von Konfliktfeldern
     */
    public static List<String> getConflictFields(final EWOBuergerDatenDto currentBuerger, final EWOBuergerDatenDto newBuerger) {

        final List<String> conflictingFields = new ArrayList<>();
        final String buergerNameLog = "Ordnungsmerkmal: " + currentBuerger.getOrdnungsmerkmal() + " | ";

        final Method[] methods = EWOBuergerDatenDto.class.getMethods();
        Arrays.stream(methods)
                /* only getter */
                .filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is"))
                /* Dont consider this getter: */
                .filter(method -> !"getKonfliktfeld".equals(method.getName()) &&
                        !"isEwoidbereitserfasst".equals(method.getName()) &&
                        !"getType".equals(method.getName()) &&
                        !"getId".equals(method.getName()) &&
                        !"getClass".equals(method.getName()))
                .forEach(getter -> {
                    final String getterName = getter.getName();
                    final String fieldName = getterName.startsWith("get") ? getterName.substring(3) : getterName.substring(2);
                    compareBuerger(currentBuerger, newBuerger, getter, buergerNameLog, fieldName, conflictingFields);
                });

        return conflictingFields;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static void compareBuerger(final EWOBuergerDatenDto currentBuerger, final EWOBuergerDatenDto newBuerger, final Method getter,
            final String buergerNameLog, final String fieldName,
            final List<String> conflictingFields) {
        try {
            if (getter.invoke(currentBuerger) != null) {
                if ("getAuskunftssperre".equals(getter.getName()) || "getStaatsangehoerigkeit".equals(getter.getName())) {

                    if (!isArrayListEqual(currentBuerger, newBuerger, getter)) {
                        log.info(buergerNameLog + "Konflikt im Feld: " + fieldName + " | Aktuell: <" + getter.invoke(currentBuerger)
                                + "> | EWO: <"
                                + getter.invoke(newBuerger) + ">");

                        conflictingFields.add(fieldName);
                    }

                } else if (!getter.invoke(currentBuerger).equals(getter.invoke(newBuerger))
                        && (!"".equals(getter.invoke(currentBuerger)) || getter.invoke(newBuerger) != null)) {

                            log.info(buergerNameLog + "Konflikt im Feld: " + fieldName + " | Aktuell: <" + getter.invoke(currentBuerger)
                                    + "> | EWO: <"
                                    + getter.invoke(newBuerger) + ">");

                            conflictingFields.add(fieldName);
                        }
            } else if (getter.invoke(newBuerger) != null) {
                log.info(buergerNameLog + "Konflikt im Feld: " + fieldName);
                conflictingFields.add(fieldName);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Exception bei Konfliktermittlung: {}", e.toString());
        }
    }

    private static boolean isArrayListEqual(final EWOBuergerDatenDto currentBuerger, final EWOBuergerDatenDto newBuerger, final Method getter)
            throws InvocationTargetException, IllegalAccessException {

        final List<String> auskunftssperreCurrentBuerger = (List<String>) getter.invoke(currentBuerger);
        final List<String> auskunftssperreNewBuerger = (List<String>) getter.invoke(newBuerger);

        if (auskunftssperreCurrentBuerger != null && auskunftssperreNewBuerger != null) {

            Collections.sort(auskunftssperreCurrentBuerger);
            Collections.sort(auskunftssperreNewBuerger);

            return auskunftssperreCurrentBuerger.equals(auskunftssperreNewBuerger);

        } else {
            return (auskunftssperreCurrentBuerger != null || auskunftssperreNewBuerger == null)
                    && (auskunftssperreCurrentBuerger == null || auskunftssperreNewBuerger != null);
        }

    }
}
