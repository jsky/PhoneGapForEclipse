package phonegapforeclipse.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;


/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class PGEWizard extends Wizard implements INewWizard {
	private PGENewWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for PGEWizard.
	 */
	public PGEWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new PGENewWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		
		IProgressMonitor monitor)
		throws CoreException {
		
		monitor.beginTask("Building PhoneGap artifacts...", 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));

		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName
					+ "\" does not exist.");
		}

		IContainer container = (IContainer) resource;
		
		/* Create Assets/www directory */
		
		
		//final IFile file = container.getFile(new Path(fileName));
		
		final IFile fileAssets = container.getFile(new Path( "assets/www/cordova-2.2.0.js" ));
		final IFile fileIndex = container.getFile(new Path( "assets/www/index.html" ));
		final IFile fileLibs = container.getFile(new Path( "libs/cordova-2.2.0.jar" ));
		final IFile fileXML = container.getFile(new Path( "res/xml/config.xml" ));
		
		try {
			//InputStream stream = openContentStream();
			//if (fileAssets.exists()) {
				//file.setContents(stream, true, true, monitor);
				
				
				
			
				
				IFolder folder = container.getProject().getFolder("assets");
				if ( !folder.exists() ) {
					/* Create it */
					folder.create(true, true, monitor );
				}
		        
		        folder = container.getProject().getFolder("assets/www");
		        
				if ( !folder.exists() ) {
					/* Create it */
					folder.create(true, true, monitor );
				}

				/* res must exist in an Android project */
		        folder = container.getProject().getFolder("res/xml");
				if ( !folder.exists() ) {
					/* Create it */
					folder.create(true, true, monitor );
				}
		        
				/* Copy the cordova.js file to assets/www */
				InputStream is = PGEWizard.class.getResourceAsStream ( "cordova-2.2.0.js" );	
				fileAssets.create( is, true, monitor);
				is.close();
				
				/* Copy the index.html file to assets/www */
				is = PGEWizard.class.getResourceAsStream ( "index.html" );	
				fileIndex.create( is, true, monitor);
				is.close();
				
				/* Now create the cordova.jar file in libs */
				is = PGEWizard.class.getResourceAsStream ( "cordova-2.2.0.jar" );	
				fileLibs.create( is, true, monitor);
				is.close();
				
				
				// now create the config.xml in XML 
				/* Now create the cordova.jar file in res/xml */
				is = PGEWizard.class.getResourceAsStream ( "config.xml" );	
				fileXML.create( is, true, monitor);
				is.close();
				
	
			
			} catch (IOException e) {
		}
/*
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		
		*/
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.mpe file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "PhoneGapForEclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}