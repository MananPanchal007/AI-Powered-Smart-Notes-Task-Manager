document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Sample data for demonstration
    let notes = [
        { id: 1, title: 'Welcome Note', content: 'Welcome to your new Smart Notes application!', date: 'Today', category: 'General' },
        { id: 2, title: 'Project Ideas', content: 'Brainstorming for new project ideas...', date: 'Yesterday', category: 'Ideas' },
        { id: 3, title: 'Meeting Notes', content: 'Discussed project timeline and deliverables...', date: '2 days ago', category: 'Work' }
    ];

    let selectedNoteId = 1;
    let isAIAssistantActive = false;

    // DOM Elements
    const notesList = document.getElementById('notesList');
    const noteTitle = document.getElementById('noteTitle');
    const editor = document.getElementById('editor');
    const newNoteBtn = document.getElementById('newNoteBtn');
    const saveNoteBtn = document.getElementById('saveNoteBtn');
    const navNotes = document.getElementById('nav-notes');
    const navTasks = document.getElementById('nav-tasks');
    const navAI = document.getElementById('nav-ai');
    const aiChat = document.getElementById('aiChat');
    const aiInput = document.getElementById('aiInput');
    const aiSendBtn = document.getElementById('aiSendBtn');
    const taskModal = new bootstrap.Modal(document.getElementById('taskModal'));

    // Initialize the app
    function initApp() {
        renderNotesList();
        loadNote(selectedNoteId);
        setupEventListeners();
    }

    // Render notes list
    function renderNotesList() {
        notesList.innerHTML = '';
        notes.forEach(note => {
            const noteElement = document.createElement('a');
            noteElement.href = '#';
            noteElement.className = `list-group-item list-group-item-action ${note.id === selectedNoteId ? 'active' : ''}`;
            noteElement.innerHTML = `
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">${note.title}</h6>
                    <small>${note.date}</small>
                </div>
                <p class="mb-1 text-truncate">${note.content.substring(0, 50)}${note.content.length > 50 ? '...' : ''}</p>
            `;
            noteElement.addEventListener('click', (e) => {
                e.preventDefault();
                selectedNoteId = note.id;
                loadNote(selectedNoteId);
                renderNotesList();
            });
            notesList.appendChild(noteElement);
        });
    }

    // Load note into editor
    function loadNote(noteId) {
        const note = notes.find(n => n.id === noteId);
        if (note) {
            noteTitle.value = note.title;
            editor.innerHTML = note.content;
        }
    }

    // Save current note
    function saveCurrentNote() {
        const noteIndex = notes.findIndex(n => n.id === selectedNoteId);
        if (noteIndex !== -1) {
            notes[noteIndex].title = noteTitle.value;
            notes[noteIndex].content = editor.innerHTML;
            showToast('Note saved successfully!');
        }
    }

    // Create a new note
    function createNewNote() {
        const newNote = {
            id: Math.max(0, ...notes.map(n => n.id)) + 1,
            title: 'Untitled Note',
            content: 'Start writing your note here...',
            date: 'Just now',
            category: 'General'
        };
        notes.unshift(newNote);
        selectedNoteId = newNote.id;
        renderNotesList();
        loadNote(selectedNoteId);
    }

    // Show toast notification
    function showToast(message) {
        // In a real app, you would implement a toast notification system
        console.log('Toast:', message);
    }

    // AI Assistant functions
    function sendAIMessage(message) {
        if (!message.trim()) return;
        
        // Add user message to chat
        addMessageToChat(message, 'user');
        
        // Simulate AI response
        setTimeout(() => {
            const responses = [
                "I've analyzed your note. Would you like me to help you improve it?",
                "I can help you organize your thoughts into actionable tasks.",
                "Would you like me to summarize this note for you?",
                "I've detected some key points in your note. Would you like me to highlight them?"
            ];
            const response = responses[Math.floor(Math.random() * responses.length)];
            addMessageToChat(response, 'ai');
        }, 1000);
        
        // Clear input
        aiInput.value = '';
    }

    function addMessageToChat(message, sender) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `${sender}-message mb-2 animate-fade-in`;
        
        const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        
        messageDiv.innerHTML = `
            <div class="d-flex ${sender === 'user' ? 'justify-content-end' : ''}">
                ${sender === 'ai' ? `
                    <div class="flex-shrink-0 me-2">
                        <i class="fas fa-robot text-primary"></i>
                    </div>
                ` : ''}
                <div style="max-width: 80%;">
                    <div class="${sender === 'user' ? 'bg-primary text-white' : 'bg-light'} p-2 rounded">
                        <p class="mb-0">${message}</p>
                    </div>
                    <small class="text-muted">${time}</small>
                </div>
            </div>
        `;
        
        aiChat.appendChild(messageDiv);
        aiChat.scrollTop = aiChat.scrollHeight;
    }

    // Event Listeners
    function setupEventListeners() {
        // Save note button
        saveNoteBtn.addEventListener('click', saveCurrentNote);
        
        // New note button
        newNoteBtn.addEventListener('click', createNewNote);
        
        // Navigation
        navNotes.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelector('.col-lg-3.d-none.d-lg-block').classList.remove('d-none');
            document.querySelector('.col-lg-6').classList.remove('col-lg-9');
            document.querySelector('.col-lg-6').classList.add('col-lg-6');
        });
        
        navTasks.addEventListener('click', (e) => {
            e.preventDefault();
            taskModal.show();
        });
        
        navAI.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelector('.col-lg-3.d-none.d-lg-block').classList.add('d-none');
            document.querySelector('.col-lg-6').classList.remove('col-lg-6');
            document.querySelector('.col-lg-6').classList.add('col-lg-9');
        });
        
        // AI Chat
        aiSendBtn.addEventListener('click', () => sendAIMessage(aiInput.value));
        aiInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                sendAIMessage(aiInput.value);
            }
        });
        
        // Save task button in modal
        document.getElementById('saveTaskBtn').addEventListener('click', () => {
            const title = document.getElementById('taskTitle').value;
            const dueDate = document.getElementById('taskDueDate').value;
            const priority = document.getElementById('taskPriority').value;
            const description = document.getElementById('taskDescription').value;
            
            if (title) {
                // In a real app, you would save the task to your backend
                showToast(`Task "${title}" created!`);
                taskModal.hide();
                
                // Reset form
                document.getElementById('taskForm').reset();
            }
        });
    }

    // Initialize the application
    initApp();
});
