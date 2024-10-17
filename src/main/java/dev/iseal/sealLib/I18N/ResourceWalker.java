package dev.iseal.sealLib.I18N;

import dev.iseal.sealLib.Utils.ExceptionHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.FileSystems.newFileSystem;
import static java.util.Collections.emptyMap;

public class ResourceWalker {

    /**
     * Globbing pattern to match message lang files.
     */
    public static final String GLOB_MESSAGES = "Messages_**_**.properties";

    /**
     * @param directory The root directory to scan for files matching the glob.
     * @param c         The consumer function to call for each matching path
     *                  found.
     * @throws URISyntaxException Could not convert the resource to a URI.
     * @throws IOException        Could not walk the tree.
     */
    public static void walk(
            final Class callingClass, final String directory, final Consumer<Path> c )
            throws URISyntaxException, IOException {
        final var resource = callingClass.getResource( directory );
        final var matcher = getDefault().getPathMatcher( "glob:" + GLOB_MESSAGES );

        if( resource != null ) {
            final var uri = resource.toURI();
            final Path path;
            FileSystem fs = null;

            if( "jar".equals( uri.getScheme() ) ) {
                fs = newFileSystem( uri, emptyMap() );
                path = fs.getPath( directory );
            }
            else {
                path = Paths.get( uri );
            }

            try( final var walk = Files.walk( path, 10 ) ) {
                for( final var it = walk.iterator(); it.hasNext(); ) {
                    final Path p = it.next();
                    if( matcher.matches( p ) ) {
                        c.accept( p );
                    }
                }
            } finally {
                if( fs != null ) { fs.close(); }
            }
        }
    }
}