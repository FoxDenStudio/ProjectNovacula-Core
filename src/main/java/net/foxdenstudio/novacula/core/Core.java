/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 * *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 * *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.core;

import net.foxdenstudio.novacula.core.plugins.PluginSystem;
import net.foxdenstudio.novacula.core.routing.Route;
import net.foxdenstudio.novacula.core.routing.RouteHandler;
import net.foxdenstudio.novacula.core.server.NovaServer;
import net.foxdenstudio.novacula.core.utils.NovaInfo;
import net.foxdenstudio.novacula.core.utils.NovaLogger;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class Core {

    private final PluginSystem pluginSystem;
    private final NovaLogger novaLogger;
    private final NovaServer novaServer;
    private final RouteHandler routeHandler;

    public Core() {
        this(new NovaLogger());
    }

    public Core(NovaLogger novaLogger) {
        this.novaLogger = novaLogger;
        this.pluginSystem = new PluginSystem(this.novaLogger);

        this.routeHandler = new RouteHandler(this.novaLogger, new Route(""), new Route("web"));

        this.novaServer = new NovaServer(this.novaLogger);
        this.novaServer.start();
    }

    public PluginSystem getPluginSystem() {
        return pluginSystem;
    }

    public NovaLogger getNovaLogger() {
        return novaLogger;
    }

    public NovaServer getNovaServer() {
        return novaServer;
    }

    public RouteHandler getRouteHandler() {
        return routeHandler;
    }
}
