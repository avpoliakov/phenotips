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

import org.phenotips.storage.migrators.DataReader;
import org.phenotips.storage.migrators.DataTypeMigrator;
import org.phenotips.storage.migrators.DataWriter;

import org.xwiki.configuration.ConfigurationSource;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

/**
 * @version $Id$
 */
public abstract class AbstractDataTypeMigrator<T> implements DataTypeMigrator<T>
{
    private static final String DEFAULT_STORE = "hibernate";

    @Inject
    private Logger logger;

    @Inject
    @Named("legacy")
    private ConfigurationSource config;

    @Inject
    protected Map<String, DataReader<T>> readers;

    @Inject
    protected Map<String, DataWriter<T>> writers;

    @Override
    public boolean migrate()
    {
        DataWriter<T> writer = getWriter();
        for (Map.Entry<String, DataReader<T>> entry : this.readers.entrySet()) {
            DataReader<T> reader = entry.getValue();
            if (!reader.hasData()) {
                continue;
            }

            if (writer == null) {
                // No writer found, keep data in place
                this.logger.warn(
                    "Legacy attachments found in [{}], but no current storage configured; keeping legacy data",
                    entry.getKey());
                return false;
            }

            Iterator<T> data = reader.getData();
            while (data.hasNext()) {
                T item = data.next();
                if (!writer.storeItem(item)) {
                }
            }
        }
        // TODO Auto-generated method stub
        return false;
    }

    private DataWriter<T> getWriter()
    {
        Object cfg = this.config.getProperty(getStoreConfigurationKey());
        this.logger.error("Config key: {}, {}", cfg, cfg.getClass().getCanonicalName());
        DataWriter<T> result = null;
        // writers.get(cfg);
        return result;
    }

    protected abstract String getStoreConfigurationKey();
}
