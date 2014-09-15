package org.phenotips.storage.migrators.internal;

import org.phenotips.storage.migrators.DataMigrationManager;
import org.phenotips.storage.migrators.DataReader;
import org.phenotips.storage.migrators.DataTypeMigrator;

import org.xwiki.component.annotation.Component;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation for the {@link DataMigrationManager} role, which tries to use all available {@link DataReader}s that
 * {@link DataReader#hasData() have legacy data} and write it to the currently enabled storage engine.
 *
 * @version $Id$
 * @since 1.0RC1
 */
@Component
@Singleton
public class AutomaticDataMigrationManager implements DataMigrationManager
{
    @Inject
    private List<DataTypeMigrator<?>> migrators;

    @Override
    public boolean migrate()
    {
        for (DataTypeMigrator<?> migrator : this.migrators) {
            migrator.migrate();
        }
        return true;
    }
}
