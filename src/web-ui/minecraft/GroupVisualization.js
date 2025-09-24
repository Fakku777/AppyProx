/**
 * Groups Visualization System - Modern UI for managing player groups
 * Features: drag-drop, color coding, collapse/expand, looping tasks
 */

class GroupVisualization {
  constructor(groupManager, logger) {
    this.groupManager = groupManager;
    this.logger = logger;
    
    // UI state
    this.draggedElement = null;
    this.dropTarget = null;
    this.isDragging = false;
    
    // Group icons
    this.groupIcons = {
      'users': '👥', 'mining': '⛏️', 'farming': '🌾', 'building': '🏗️',
      'combat': '⚔️', 'exploring': '🗺️', 'trading': '💰', 'crafting': '🔨'
    };
    
    // Initialize UI if DOM is available
    this.isDOMAvailable = typeof document !== 'undefined';
    if (this.isDOMAvailable) {
      this.initializeGroupsUI();
      this.setupEventListeners();
    }
  }

  initializeGroupsUI() {
    this.createGroupsContainer();
    this.createGroupCreationModal();
    this.createTaskModal();
    this.createContextMenu();
  }

  createGroupsContainer() {
    const existing = document.getElementById('groups-container');
    if (existing) existing.remove();

    const container = document.createElement('div');
    container.id = 'groups-container';
    container.className = 'minecraft-groups-container';
    
    container.innerHTML = `
      <div class="groups-header minecraft-panel">
        <div class="groups-title">
          <h3 class="minecraft-title">Groups</h3>
          <span id="groups-count" class="minecraft-badge">0</span>
        </div>
        <div class="groups-controls">
          <button id="create-group-btn" class="minecraft-button-primary">+ New Group</button>
          <button id="collapse-all-groups-btn" class="minecraft-button">⇅</button>
        </div>
      </div>
      
      <div class="groups-list" id="groups-list">
        <!-- Groups will be populated here -->
      </div>
      
      <div class="groups-footer minecraft-panel">
        <div class="groups-stats">
          <span class="stat">Total Members: <span id="total-members">0</span></span>
          <span class="stat">Active Tasks: <span id="active-tasks">0</span></span>
        </div>
      </div>
    `;

    // Insert into left panel
    const leftPanel = document.getElementById('left-panel');
    if (leftPanel) {
      leftPanel.appendChild(container);
    } else {
      document.body.appendChild(container);
    }
  }

  createGroupCreationModal() {
    const existing = document.getElementById('create-group-modal');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.id = 'create-group-modal';
    modal.className = 'minecraft-modal-overlay hidden';
    
    modal.innerHTML = `
      <div class="minecraft-modal minecraft-window">
        <div class="modal-header">
          <h3 class="minecraft-title">Create New Group</h3>
          <button id="close-create-group-modal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-content">
          <div class="form-group">
            <label class="minecraft-label">Group Name:</label>
            <input type="text" id="group-name-input" class="minecraft-input" placeholder="Enter group name" maxlength="20">
          </div>
          
          <div class="form-group">
            <label class="minecraft-label">Color:</label>
            <div class="color-picker" id="group-color-picker">
              <!-- Color options will be populated -->
            </div>
          </div>
          
          <div class="form-group">
            <label class="minecraft-label">Icon:</label>
            <div class="icon-picker" id="group-icon-picker">
              <!-- Icon options will be populated -->
            </div>
          </div>
          
          <div class="form-group">
            <label class="minecraft-label">Max Members:</label>
            <input type="number" id="group-max-size" class="minecraft-input" min="1" max="20" value="5">
          </div>
          
          <div class="form-group">
            <label class="minecraft-label">Settings:</label>
            <div class="settings-checkboxes">
              <label class="minecraft-checkbox">
                <input type="checkbox" id="follow-leader"> Follow Leader
              </label>
              <label class="minecraft-checkbox">
                <input type="checkbox" id="auto-reconnect" checked> Auto Reconnect
              </label>
              <label class="minecraft-checkbox">
                <input type="checkbox" id="share-inventory"> Share Inventory
              </label>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button id="create-group-confirm" class="minecraft-button-primary">Create Group</button>
          <button id="create-group-cancel" class="minecraft-button">Cancel</button>
        </div>
      </div>
    `;
    
    document.body.appendChild(modal);
    this.populateColorPicker();
    this.populateIconPicker();
  }

  createTaskModal() {
    const existing = document.getElementById('group-task-modal');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.id = 'group-task-modal';
    modal.className = 'minecraft-modal-overlay hidden';
    
    modal.innerHTML = `
      <div class="minecraft-modal minecraft-window">
        <div class="modal-header">
          <h3 class="minecraft-title">Assign Task to Group</h3>
          <button id="close-task-modal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-content">
          <div class="form-group">
            <label class="minecraft-label">Task Type:</label>
            <select id="task-type-select" class="minecraft-dropdown">
              <option value="gather">Gather Resources</option>
              <option value="mine">Mine Blocks</option>
              <option value="farm">Farm Crops</option>
              <option value="build">Build Structure</option>
              <option value="explore">Explore Area</option>
              <option value="follow">Follow Player</option>
            </select>
          </div>
          
          <div class="form-group">
            <label class="minecraft-checkbox">
              <input type="checkbox" id="task-is-looping"> Looping Task
            </label>
          </div>
          
          <div id="looping-options" class="form-group hidden">
            <label class="minecraft-label">Max Loops (0 = infinite):</label>
            <input type="number" id="task-max-loops" class="minecraft-input" min="0" value="0">
            
            <label class="minecraft-label">Delay Between Loops (ms):</label>
            <input type="number" id="task-loop-delay" class="minecraft-input" min="1000" value="5000">
          </div>
          
          <div class="form-group">
            <label class="minecraft-label">Task Parameters:</label>
            <textarea id="task-parameters" class="minecraft-textarea" placeholder="Enter task parameters as JSON"></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button id="assign-task-confirm" class="minecraft-button-primary">Assign Task</button>
          <button id="assign-task-cancel" class="minecraft-button">Cancel</button>
        </div>
      </div>
    `;
    
    document.body.appendChild(modal);
  }

  createContextMenu() {
    const existing = document.getElementById('group-context-menu');
    if (existing) existing.remove();

    const menu = document.createElement('div');
    menu.id = 'group-context-menu';
    menu.className = 'context-menu hidden';
    
    menu.innerHTML = `
      <div class="menu-item" data-action="assign-task">Assign Task</div>
      <div class="menu-item" data-action="toggle-collapse">Toggle Collapse</div>
      <div class="menu-item" data-action="change-color">Change Color</div>
      <div class="menu-item" data-action="change-icon">Change Icon</div>
      <div class="menu-separator"></div>
      <div class="menu-item" data-action="view-stats">View Stats</div>
      <div class="menu-item" data-action="export-group">Export Group</div>
      <div class="menu-separator"></div>
      <div class="menu-item danger" data-action="delete-group">Delete Group</div>
    `;
    
    document.body.appendChild(menu);
  }

  populateColorPicker() {
    const picker = document.getElementById('group-color-picker');
    if (!picker) return;

    const colors = [
      '#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', 
      '#ef4444', '#06b6d4', '#84cc16', '#f97316',
      '#ec4899', '#6366f1', '#14b8a6', '#eab308'
    ];

    colors.forEach(color => {
      const colorOption = document.createElement('div');
      colorOption.className = 'color-option';
      colorOption.style.backgroundColor = color;
      colorOption.dataset.color = color;
      picker.appendChild(colorOption);
    });
  }

  populateIconPicker() {
    const picker = document.getElementById('group-icon-picker');
    if (!picker) return;

    Object.entries(this.groupIcons).forEach(([key, emoji]) => {
      const iconOption = document.createElement('div');
      iconOption.className = 'icon-option';
      iconOption.textContent = emoji;
      iconOption.dataset.icon = key;
      iconOption.title = key;
      picker.appendChild(iconOption);
    });
  }

  setupEventListeners() {
    // Group creation
    document.addEventListener('click', (e) => {
      if (e.target.id === 'create-group-btn') {
        this.showCreateGroupModal();
      } else if (e.target.id === 'create-group-confirm') {
        this.createGroup();
      } else if (e.target.id === 'create-group-cancel' || e.target.id === 'close-create-group-modal') {
        this.hideCreateGroupModal();
      }
    });

    // Color and icon selection
    document.addEventListener('click', (e) => {
      if (e.target.classList.contains('color-option')) {
        document.querySelectorAll('.color-option').forEach(opt => opt.classList.remove('selected'));
        e.target.classList.add('selected');
      } else if (e.target.classList.contains('icon-option')) {
        document.querySelectorAll('.icon-option').forEach(opt => opt.classList.remove('selected'));
        e.target.classList.add('selected');
      }
    });

    // Task modal
    document.addEventListener('click', (e) => {
      if (e.target.id === 'close-task-modal' || e.target.id === 'assign-task-cancel') {
        this.hideTaskModal();
      } else if (e.target.id === 'assign-task-confirm') {
        this.assignTask();
      }
    });

    document.addEventListener('change', (e) => {
      if (e.target.id === 'task-is-looping') {
        const loopingOptions = document.getElementById('looping-options');
        if (loopingOptions) {
          loopingOptions.classList.toggle('hidden', !e.target.checked);
        }
      }
    });

    // Context menu
    document.addEventListener('contextmenu', (e) => {
      if (e.target.closest('.group-item')) {
        e.preventDefault();
        this.showContextMenu(e, e.target.closest('.group-item'));
      }
    });

    document.addEventListener('click', (e) => {
      if (e.target.classList.contains('menu-item')) {
        this.handleContextMenuAction(e.target.dataset.action);
        this.hideContextMenu();
      } else if (!e.target.closest('.context-menu')) {
        this.hideContextMenu();
      }
    });

    // Drag and drop
    this.setupDragAndDrop();

    // Collapse all
    document.addEventListener('click', (e) => {
      if (e.target.id === 'collapse-all-groups-btn') {
        this.toggleAllGroups();
      }
    });

    // Modal close on overlay click
    document.addEventListener('click', (e) => {
      if (e.target.classList.contains('minecraft-modal-overlay')) {
        this.hideAllModals();
      }
    });
  }

  setupDragAndDrop() {
    // Enable drag and drop for players
    document.addEventListener('dragstart', (e) => {
      if (e.target.classList.contains('group-member')) {
        this.draggedElement = e.target;
        this.isDragging = true;
        e.target.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/html', e.target.outerHTML);
      }
    });

    document.addEventListener('dragover', (e) => {
      if (this.isDragging && e.target.closest('.group-members, .group-item')) {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
        
        const dropZone = e.target.closest('.group-members, .group-item');
        if (dropZone && dropZone !== this.dropTarget) {
          document.querySelectorAll('.drop-target').forEach(el => el.classList.remove('drop-target'));
          dropZone.classList.add('drop-target');
          this.dropTarget = dropZone;
        }
      }
    });

    document.addEventListener('drop', (e) => {
      if (this.isDragging && this.draggedElement && this.dropTarget) {
        e.preventDefault();
        
        const clientId = this.draggedElement.dataset.clientId;
        const targetGroupId = this.dropTarget.closest('.group-item').dataset.groupId;
        
        if (clientId && targetGroupId) {
          this.moveClientToGroup(clientId, targetGroupId);
        }
      }
      
      this.cleanupDragState();
    });

    document.addEventListener('dragend', () => {
      this.cleanupDragState();
    });
  }

  cleanupDragState() {
    if (this.draggedElement) {
      this.draggedElement.classList.remove('dragging');
    }
    
    document.querySelectorAll('.drop-target').forEach(el => el.classList.remove('drop-target'));
    
    this.draggedElement = null;
    this.dropTarget = null;
    this.isDragging = false;
  }

  // Group Management UI Methods
  renderGroups(groups) {
    const groupsList = document.getElementById('groups-list');
    if (!groupsList) return;

    groupsList.innerHTML = '';
    
    if (!groups || groups.length === 0) {
      groupsList.innerHTML = `
        <div class="no-groups minecraft-text-small">
          No groups created yet. Click "New Group" to get started!
        </div>
      `;
      return;
    }

    groups.forEach(group => {
      const groupElement = this.createGroupElement(group);
      groupsList.appendChild(groupElement);
    });

    this.updateGroupsStats(groups);
  }

  createGroupElement(group) {
    const groupDiv = document.createElement('div');
    groupDiv.className = `group-item minecraft-window ${group.collapsed ? 'collapsed' : ''}`;
    groupDiv.dataset.groupId = group.id;
    groupDiv.style.borderLeftColor = group.color;

    const onlineMembers = group.members.filter(memberId => {
      // This would check actual client status
      return true; // Simplified for now
    });

    groupDiv.innerHTML = `
      <div class="group-header" style="background: linear-gradient(90deg, ${group.color}15 0%, transparent 100%);">
        <div class="group-info">
          <div class="group-icon" style="color: ${group.color};">${this.groupIcons[group.icon] || '👥'}</div>
          <div class="group-details">
            <h4 class="group-name minecraft-text">${group.name}</h4>
            <span class="group-member-count minecraft-text-small">${onlineMembers.length}/${group.members.length} online</span>
          </div>
        </div>
        <div class="group-actions">
          <button class="group-collapse-btn minecraft-button" title="${group.collapsed ? 'Expand' : 'Collapse'}">
            ${group.collapsed ? '▶' : '▼'}
          </button>
          <button class="group-task-btn minecraft-button" title="Assign Task">⚡</button>
        </div>
      </div>
      
      <div class="group-body ${group.collapsed ? 'hidden' : ''}">
        <div class="group-members">
          ${this.renderGroupMembers(group)}
        </div>
        
        ${group.currentTasks && group.currentTasks.length > 0 ? `
          <div class="group-tasks">
            <h5 class="minecraft-subtitle">Active Tasks</h5>
            ${this.renderGroupTasks(group.currentTasks)}
          </div>
        ` : ''}
        
        ${group.loopingTask ? `
          <div class="group-looping-task">
            <h5 class="minecraft-subtitle">Looping Task</h5>
            <div class="looping-task-info">
              <span class="task-name">${group.loopingTask.type}</span>
              <span class="loop-count">Loop: ${group.loopingTask.loopCount || 0}</span>
            </div>
          </div>
        ` : ''}
      </div>
    `;

    return groupDiv;
  }

  renderGroupMembers(group) {
    if (!group.members || group.members.length === 0) {
      return '<div class="no-members minecraft-text-small">No members yet</div>';
    }

    return group.members.map(memberId => {
      // This would get actual client info
      const memberName = `Player_${memberId.substring(0, 8)}`;
      const isLeader = group.leader === memberId;
      const isOnline = true; // Simplified for now

      return `
        <div class="group-member ${isLeader ? 'leader' : ''} ${isOnline ? 'online' : 'offline'}" 
             draggable="true" data-client-id="${memberId}">
          <div class="member-status ${isOnline ? 'online' : 'offline'}"></div>
          <span class="member-name">${memberName}</span>
          ${isLeader ? '<span class="leader-badge">👑</span>' : ''}
        </div>
      `;
    }).join('');
  }

  renderGroupTasks(tasks) {
    return tasks.map(task => `
      <div class="group-task">
        <div class="task-info">
          <span class="task-type">${task.type}</span>
          <div class="task-progress minecraft-progress-bar">
            <div class="minecraft-progress-fill" style="width: ${task.progress || 0}%"></div>
          </div>
        </div>
        <button class="cancel-task-btn" data-task-id="${task.id}">×</button>
      </div>
    `).join('');
  }

  updateGroupsStats(groups) {
    const totalMembers = groups.reduce((sum, group) => sum + group.members.length, 0);
    const activeTasks = groups.reduce((sum, group) => sum + (group.currentTasks?.length || 0), 0);

    const totalMembersEl = document.getElementById('total-members');
    const activeTasksEl = document.getElementById('active-tasks');
    const groupsCountEl = document.getElementById('groups-count');

    if (totalMembersEl) totalMembersEl.textContent = totalMembers;
    if (activeTasksEl) activeTasksEl.textContent = activeTasks;
    if (groupsCountEl) groupsCountEl.textContent = groups.length;
  }

  // Modal Management
  showCreateGroupModal() {
    const modal = document.getElementById('create-group-modal');
    if (modal) {
      modal.classList.remove('hidden');
      document.getElementById('group-name-input')?.focus();
    }
  }

  hideCreateGroupModal() {
    const modal = document.getElementById('create-group-modal');
    if (modal) {
      modal.classList.add('hidden');
      this.resetCreateGroupForm();
    }
  }

  resetCreateGroupForm() {
    const nameInput = document.getElementById('group-name-input');
    const maxSizeInput = document.getElementById('group-max-size');
    
    if (nameInput) nameInput.value = '';
    if (maxSizeInput) maxSizeInput.value = '5';
    
    document.querySelectorAll('.color-option, .icon-option').forEach(opt => {
      opt.classList.remove('selected');
    });
    
    document.querySelectorAll('input[type="checkbox"]').forEach(cb => {
      cb.checked = cb.id === 'auto-reconnect';
    });
  }

  showTaskModal(groupId) {
    this.currentGroupId = groupId;
    const modal = document.getElementById('group-task-modal');
    if (modal) {
      modal.classList.remove('hidden');
    }
  }

  hideTaskModal() {
    const modal = document.getElementById('group-task-modal');
    if (modal) {
      modal.classList.add('hidden');
      this.resetTaskForm();
    }
  }

  resetTaskForm() {
    const taskTypeSelect = document.getElementById('task-type-select');
    const isLoopingCheckbox = document.getElementById('task-is-looping');
    const loopingOptions = document.getElementById('looping-options');
    const parametersTextarea = document.getElementById('task-parameters');

    if (taskTypeSelect) taskTypeSelect.selectedIndex = 0;
    if (isLoopingCheckbox) isLoopingCheckbox.checked = false;
    if (loopingOptions) loopingOptions.classList.add('hidden');
    if (parametersTextarea) parametersTextarea.value = '';
  }

  hideAllModals() {
    document.querySelectorAll('.minecraft-modal-overlay').forEach(modal => {
      modal.classList.add('hidden');
    });
  }

  // Action Handlers
  createGroup() {
    const nameInput = document.getElementById('group-name-input');
    const maxSizeInput = document.getElementById('group-max-size');
    const selectedColor = document.querySelector('.color-option.selected')?.dataset.color;
    const selectedIcon = document.querySelector('.icon-option.selected')?.dataset.icon;

    if (!nameInput?.value.trim()) {
      alert('Please enter a group name');
      return;
    }

    const groupOptions = {
      maxSize: parseInt(maxSizeInput?.value || '5'),
      color: selectedColor,
      icon: selectedIcon,
      followLeader: document.getElementById('follow-leader')?.checked,
      autoReconnect: document.getElementById('auto-reconnect')?.checked,
      shareInventory: document.getElementById('share-inventory')?.checked
    };

    if (this.groupManager) {
      const groupId = this.groupManager.createGroup(nameInput.value.trim(), groupOptions);
      this.logger?.info(`Created group: ${nameInput.value} (${groupId})`);
    }

    this.hideCreateGroupModal();
    this.emit('group_created', { name: nameInput.value.trim(), options: groupOptions });
  }

  assignTask() {
    if (!this.currentGroupId) return;

    const taskTypeSelect = document.getElementById('task-type-select');
    const isLoopingCheckbox = document.getElementById('task-is-looping');
    const maxLoopsInput = document.getElementById('task-max-loops');
    const loopDelayInput = document.getElementById('task-loop-delay');
    const parametersTextarea = document.getElementById('task-parameters');

    let parameters = {};
    try {
      if (parametersTextarea?.value.trim()) {
        parameters = JSON.parse(parametersTextarea.value);
      }
    } catch (error) {
      alert('Invalid JSON in task parameters');
      return;
    }

    const taskConfig = {
      type: taskTypeSelect?.value || 'gather',
      parameters,
      isLooping: isLoopingCheckbox?.checked || false,
      maxLoops: parseInt(maxLoopsInput?.value || '0'),
      loopDelay: parseInt(loopDelayInput?.value || '5000')
    };

    if (this.groupManager) {
      const taskId = this.groupManager.assignTaskToGroup(this.currentGroupId, taskConfig);
      this.logger?.info(`Assigned task to group: ${taskConfig.type} (${taskId})`);
    }

    this.hideTaskModal();
    this.emit('task_assigned', { groupId: this.currentGroupId, taskConfig });
  }

  toggleAllGroups() {
    const groupItems = document.querySelectorAll('.group-item');
    const hasExpanded = Array.from(groupItems).some(item => !item.classList.contains('collapsed'));

    groupItems.forEach(item => {
      const groupId = item.dataset.groupId;
      if (hasExpanded) {
        item.classList.add('collapsed');
        if (this.groupManager) {
          this.groupManager.toggleGroupCollapse(groupId);
        }
      } else {
        item.classList.remove('collapsed');
        if (this.groupManager) {
          this.groupManager.toggleGroupCollapse(groupId);
        }
      }
    });
  }

  moveClientToGroup(clientId, targetGroupId) {
    if (this.groupManager) {
      const success = this.groupManager.moveClientToGroup(clientId, targetGroupId);
      if (success) {
        this.emit('client_moved', { clientId, targetGroupId });
      }
    }
  }

  // Context Menu
  showContextMenu(event, groupElement) {
    this.contextTargetGroup = groupElement.dataset.groupId;
    const menu = document.getElementById('group-context-menu');
    if (menu) {
      menu.style.left = `${event.pageX}px`;
      menu.style.top = `${event.pageY}px`;
      menu.classList.remove('hidden');
    }
  }

  hideContextMenu() {
    const menu = document.getElementById('group-context-menu');
    if (menu) {
      menu.classList.add('hidden');
    }
  }

  handleContextMenuAction(action) {
    if (!this.contextTargetGroup) return;

    switch (action) {
      case 'assign-task':
        this.showTaskModal(this.contextTargetGroup);
        break;
      case 'toggle-collapse':
        if (this.groupManager) {
          this.groupManager.toggleGroupCollapse(this.contextTargetGroup);
        }
        break;
      case 'delete-group':
        if (confirm('Are you sure you want to delete this group?')) {
          if (this.groupManager) {
            this.groupManager.deleteGroup(this.contextTargetGroup);
          }
        }
        break;
      // Add more actions as needed
    }
  }

  // Event emitter pattern
  emit(eventName, data) {
    if (this.onEvent) {
      this.onEvent(eventName, data);
    }
  }

  // Public methods for external updates
  updateGroups(groups) {
    if (this.isDOMAvailable) {
      this.renderGroups(groups);
    }
  }

  setEventHandler(handler) {
    this.onEvent = handler;
  }
}

module.exports = GroupVisualization;