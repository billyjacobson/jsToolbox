package com.karateca.jstoolbox.generatemethod;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.EventDispatcher;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez
 */
class ParentNamespaceFinder {

  private final Document document;
  private final EventDispatcher<ChangeListener> myEventDispatcher =
      EventDispatcher.create(ChangeListener.class);
  private final Project project;

  private String currentNamespace;
  private String parentNamespace;
  private List<Function> functionNames;

  public ParentNamespaceFinder(Document document, Project project) {
    this.document = document;
    this.project = project;
  }

  public String getCurrentNamespace() {
    return currentNamespace;
  }

  public String getParentNamespace() {
    return parentNamespace;
  }

  public List<Function> getFunctionNames() {
    return functionNames;
  }


  public void findParentClass() {
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        functionNames = new ArrayList<Function>();

        HierarchyFinder hierarchyFinder = new HierarchyFinder(project, document);
        List<Document> parents = hierarchyFinder.findParents();

        for (Document doc : parents) {
          ClassFinder finder = new ClassFinder(doc);
          functionNames.addAll(finder.getMethods());
        }

        broadcastEvent();
      }
    });
  }


  private void broadcastEvent() {
    myEventDispatcher.getMulticaster().stateChanged(new ChangeEvent("ParentNamespaceFound"));
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }

}
