#!/usr/bin/env node
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const archiver = require('archiver');

class BackupManager {
  constructor() {
    this.projectRoot = path.join(__dirname, '..');
    this.backupDir = path.join(this.projectRoot, '..', 'backups');
    this.packageInfo = require('../package.json');
  }

  generateVersionName() {
    const version = this.packageInfo.version;
    const [release, beta, alpha] = version.split('.').map(Number);
    
    return `AppyProx-Alpha-${release}.${beta}.${alpha}`;
  }

  ensureBackupDirectory() {
    if (!fs.existsSync(this.backupDir)) {
      fs.mkdirSync(this.backupDir, { recursive: true });
    }
  }

  async createBackup(message = 'Automated backup') {
    try {
      const versionName = this.generateVersionName();
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const backupName = `${versionName}_${timestamp}`;
      const backupPath = path.join(this.backupDir, `${backupName}.tar.gz`);

      this.ensureBackupDirectory();

      console.log(`Creating backup: ${backupName}`);
      console.log(`Backup message: ${message}`);

      // Create git tag for this version
      try {
        execSync(`git add .`, { cwd: this.projectRoot });
        execSync(`git commit -m "${message}"`, { cwd: this.projectRoot });
        execSync(`git tag -a ${versionName} -m "${message}"`, { cwd: this.projectRoot });
        console.log(`✓ Git tag created: ${versionName}`);
      } catch (error) {
        console.log(`⚠ Git operations failed: ${error.message}`);
      }

      // Create compressed archive
      const output = fs.createWriteStream(backupPath);
      const archive = archiver('tar', { 
        gzip: true,
        gzipOptions: { level: 9 }
      });

      output.on('close', () => {
        const sizeInMB = (archive.pointer() / 1024 / 1024).toFixed(2);
        console.log(`✓ Backup created: ${backupPath} (${sizeInMB} MB)`);
      });

      archive.on('error', (err) => {
        throw err;
      });

      archive.pipe(output);

      // Add files to archive, excluding node_modules and other unnecessary files
      archive.glob('**/*', {
        cwd: this.projectRoot,
        ignore: [
          'node_modules/**',
          'logs/**',
          'cache/**',
          'data/**',
          '.git/**',
          'backups/**',
          '*.log'
        ]
      });

      await archive.finalize();

      // Clean up old backups (keep only last 10)
      this.cleanupOldBackups();

      console.log('✓ Backup process completed successfully');

    } catch (error) {
      console.error('❌ Backup failed:', error.message);
      process.exit(1);
    }
  }

  cleanupOldBackups() {
    try {
      const backupFiles = fs.readdirSync(this.backupDir)
        .filter(file => file.startsWith('AppyProx-Alpha-') && file.endsWith('.tar.gz'))
        .map(file => ({
          name: file,
          path: path.join(this.backupDir, file),
          stats: fs.statSync(path.join(this.backupDir, file))
        }))
        .sort((a, b) => b.stats.mtime - a.stats.mtime);

      if (backupFiles.length > 10) {
        const filesToDelete = backupFiles.slice(10);
        filesToDelete.forEach(file => {
          fs.unlinkSync(file.path);
          console.log(`🗑️  Removed old backup: ${file.name}`);
        });
      }
    } catch (error) {
      console.warn('⚠ Could not clean up old backups:', error.message);
    }
  }

  listBackups() {
    if (!fs.existsSync(this.backupDir)) {
      console.log('No backups directory found.');
      return;
    }

    const backups = fs.readdirSync(this.backupDir)
      .filter(file => file.startsWith('AppyProx-Alpha-') && file.endsWith('.tar.gz'))
      .map(file => {
        const stats = fs.statSync(path.join(this.backupDir, file));
        const sizeInMB = (stats.size / 1024 / 1024).toFixed(2);
        return {
          name: file,
          created: stats.mtime.toISOString(),
          size: `${sizeInMB} MB`
        };
      })
      .sort((a, b) => new Date(b.created) - new Date(a.created));

    if (backups.length === 0) {
      console.log('No backups found.');
      return;
    }

    console.log('Available backups:');
    console.log('─'.repeat(80));
    backups.forEach((backup, index) => {
      console.log(`${index + 1}. ${backup.name}`);
      console.log(`   Created: ${backup.created}`);
      console.log(`   Size: ${backup.size}`);
      console.log('');
    });
  }
}

// CLI Interface
const args = process.argv.slice(2);
const command = args[0];
const message = args.slice(1).join(' ') || 'Automated backup';

const backupManager = new BackupManager();

switch (command) {
  case 'create':
  case undefined:
    backupManager.createBackup(message);
    break;
  case 'list':
    backupManager.listBackups();
    break;
  case 'help':
    console.log(`
AppyProx Backup Manager

Usage:
  node scripts/backup.js [command] [message]

Commands:
  create    Create a new backup (default)
  list      List all available backups
  help      Show this help message

Examples:
  node scripts/backup.js create "Initial project setup"
  node scripts/backup.js list
    `);
    break;
  default:
    console.error(`Unknown command: ${command}`);
    process.exit(1);
}