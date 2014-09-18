/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
    private List<DataTypeMigrator> migrators;

    @Override
    public boolean migrate()
    {
        for (DataTypeMigrator<?> migrator : this.migrators) {
            migrator.migrate();
        }
        return true;
    }
}
