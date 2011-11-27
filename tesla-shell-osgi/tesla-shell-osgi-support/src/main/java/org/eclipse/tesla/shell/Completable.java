package org.eclipse.tesla.shell;

import java.util.List;
import java.util.Map;

import org.apache.karaf.shell.console.Completer;

/**
 * TODO
 *
 * @since 1.0
 */
public interface Completable
{

    List<? extends Completer> getCompleters();

    Map<String, ? extends Completer> getOptionalCompleters();

}
