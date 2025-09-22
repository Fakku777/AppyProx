const fs = require('fs');
const path = require('path');

/**
 * Enhanced logging utility with file rotation and multiple log levels
 */
class Logger {
  constructor(config) {
    this.config = {
      level: 'info',
      file: 'logs/appyprox.log',
      maxFileSize: '10mb',
      maxFiles: 5,
      ...config
    };

    this.levels = {
      error: 0,
      warn: 1,
      info: 2,
      debug: 3
    };

    this.currentLevel = this.levels[this.config.level] || this.levels.info;
    this.logFile = path.resolve(this.config.file);
    
    // Ensure log directory exists
    this.ensureLogDirectory();
  }

  ensureLogDirectory() {
    const logDir = path.dirname(this.logFile);
    if (!fs.existsSync(logDir)) {
      fs.mkdirSync(logDir, { recursive: true });
    }
  }

  formatMessage(level, message, meta = {}) {
    const timestamp = new Date().toISOString();
    const metaStr = Object.keys(meta).length > 0 ? ` ${JSON.stringify(meta)}` : '';
    return `[${timestamp}] [${level.toUpperCase()}] ${message}${metaStr}`;
  }

  shouldLog(level) {
    return this.levels[level] <= this.currentLevel;
  }

  writeToFile(formattedMessage) {
    try {
      // Check file size and rotate if necessary
      if (fs.existsSync(this.logFile)) {
        const stats = fs.statSync(this.logFile);
        const maxSizeBytes = this.parseFileSize(this.config.maxFileSize);
        
        if (stats.size > maxSizeBytes) {
          this.rotateLogFiles();
        }
      }

      fs.appendFileSync(this.logFile, formattedMessage + '\n');
    } catch (error) {
      console.error('Failed to write to log file:', error.message);
    }
  }

  rotateLogFiles() {
    try {
      // Rotate existing log files
      for (let i = this.config.maxFiles - 1; i >= 1; i--) {
        const currentFile = `${this.logFile}.${i}`;
        const nextFile = `${this.logFile}.${i + 1}`;
        
        if (fs.existsSync(currentFile)) {
          if (i === this.config.maxFiles - 1) {
            // Delete the oldest log file
            fs.unlinkSync(currentFile);
          } else {
            fs.renameSync(currentFile, nextFile);
          }
        }
      }

      // Move current log to .1
      if (fs.existsSync(this.logFile)) {
        fs.renameSync(this.logFile, `${this.logFile}.1`);
      }
    } catch (error) {
      console.error('Failed to rotate log files:', error.message);
    }
  }

  parseFileSize(sizeStr) {
    const units = {
      'b': 1,
      'kb': 1024,
      'mb': 1024 * 1024,
      'gb': 1024 * 1024 * 1024
    };

    const match = sizeStr.toLowerCase().match(/^(\d+(?:\.\d+)?)\s*([kmg]?b)$/);
    if (!match) return 10 * 1024 * 1024; // Default 10MB

    const [, size, unit] = match;
    return parseFloat(size) * (units[unit] || 1);
  }

  log(level, message, meta = {}) {
    if (!this.shouldLog(level)) return;

    const formattedMessage = this.formatMessage(level, message, meta);
    
    // Console output with colors
    const colors = {
      error: '\x1b[31m',  // Red
      warn: '\x1b[33m',   // Yellow
      info: '\x1b[36m',   // Cyan
      debug: '\x1b[90m'   // Gray
    };
    
    const resetColor = '\x1b[0m';
    const coloredMessage = `${colors[level] || ''}${formattedMessage}${resetColor}`;
    
    console.log(coloredMessage);

    // File output
    this.writeToFile(formattedMessage);
  }

  error(message, meta = {}) {
    this.log('error', message, meta);
  }

  warn(message, meta = {}) {
    this.log('warn', message, meta);
  }

  info(message, meta = {}) {
    this.log('info', message, meta);
  }

  debug(message, meta = {}) {
    this.log('debug', message, meta);
  }

  // Special method for logging with custom context
  logWithContext(level, context, message, meta = {}) {
    const contextualMessage = `[${context}] ${message}`;
    this.log(level, contextualMessage, meta);
  }

  // Create child logger with context
  child(context) {
    return {
      error: (msg, meta) => this.logWithContext('error', context, msg, meta),
      warn: (msg, meta) => this.logWithContext('warn', context, msg, meta),
      info: (msg, meta) => this.logWithContext('info', context, msg, meta),
      debug: (msg, meta) => this.logWithContext('debug', context, msg, meta)
    };
  }

  // Get recent logs
  getRecentLogs(lines = 100) {
    try {
      if (!fs.existsSync(this.logFile)) {
        return [];
      }

      const content = fs.readFileSync(this.logFile, 'utf8');
      const logLines = content.trim().split('\n');
      
      return logLines.slice(-lines).map(line => {
        try {
          // Parse log line
          const match = line.match(/^\[(.+?)\] \[(.+?)\] (.+)$/);
          if (match) {
            const [, timestamp, level, message] = match;
            return {
              timestamp: new Date(timestamp),
              level: level.toLowerCase(),
              message: message
            };
          }
          return { raw: line };
        } catch {
          return { raw: line };
        }
      });
    } catch (error) {
      this.error('Failed to read recent logs:', { error: error.message });
      return [];
    }
  }

  // Clear log files
  clearLogs() {
    try {
      // Remove main log file
      if (fs.existsSync(this.logFile)) {
        fs.unlinkSync(this.logFile);
      }

      // Remove rotated log files
      for (let i = 1; i <= this.config.maxFiles; i++) {
        const rotatedFile = `${this.logFile}.${i}`;
        if (fs.existsSync(rotatedFile)) {
          fs.unlinkSync(rotatedFile);
        }
      }

      this.info('Log files cleared');
    } catch (error) {
      this.error('Failed to clear log files:', { error: error.message });
    }
  }

  // Get log statistics
  getStats() {
    try {
      const stats = {
        totalFiles: 0,
        totalSize: 0,
        files: []
      };

      // Check main log file
      if (fs.existsSync(this.logFile)) {
        const fileStats = fs.statSync(this.logFile);
        stats.totalFiles++;
        stats.totalSize += fileStats.size;
        stats.files.push({
          name: path.basename(this.logFile),
          size: fileStats.size,
          modified: fileStats.mtime
        });
      }

      // Check rotated files
      for (let i = 1; i <= this.config.maxFiles; i++) {
        const rotatedFile = `${this.logFile}.${i}`;
        if (fs.existsSync(rotatedFile)) {
          const fileStats = fs.statSync(rotatedFile);
          stats.totalFiles++;
          stats.totalSize += fileStats.size;
          stats.files.push({
            name: path.basename(rotatedFile),
            size: fileStats.size,
            modified: fileStats.mtime
          });
        }
      }

      return stats;
    } catch (error) {
      this.error('Failed to get log stats:', { error: error.message });
      return null;
    }
  }
}

module.exports = Logger;